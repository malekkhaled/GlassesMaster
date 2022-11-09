package com.ms.glassesmaster.azure;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.glassesmaster.exception.FaceDoesNotExistException;
import com.ms.glassesmaster.exception.FaceNotAlignedException;
import com.ms.glassesmaster.exception.GlassesAlreadyOnException;
import com.ms.glassesmaster.exception.TooManyFacesException;
import com.ms.glassesmaster.faces.Face;
import com.ms.glassesmaster.faces.Face.Attributes.Pose;

@Service
public class AzureService {
	
	// Tokens from Azure Key Vault
	/*
	@Value("${faceToken}")
	private String faceToken;
	@Value("${storageSAS}")
	private String storageSAS;
	@Value("${mlToken}")
	private String mlToken;
	*/
	
	private final Map<String, String> TOKENS = Map.of(
			"FaceAPI", "<face-api-token>",
			"StorageAcc", "<storage-sas-token>",
			"MLService", "<ml-service-token>"
			);

	
	// detectFace:  	Calls the Azure FaceAPI with a given image and gets response
	// inputs: 			File file = Java File object containing the local path to the image being sent
	// outputs: 		Face = Face object with requested attributes obtained from response body; null = no face detected or more than one face detected
	public Face detectFace(File file) throws IOException, InterruptedException, URISyntaxException, FaceDoesNotExistException, TooManyFacesException, FaceNotAlignedException, GlassesAlreadyOnException {
		HttpClient client = HttpClient.newHttpClient();
		
		// Create HttpRequest for Azure Face API
		HttpRequest faceReq = HttpRequest.newBuilder()
				.uri(new URI("https://usf-capstone-face-info.cognitiveservices.azure.com/face/v1.0/detect"
									+ "?overload=stream&returnFaceId=True&returnFaceLandmarks=True&returnFaceAttributes=glasses,headPose"))
				.header("Content-Type", "application/octet-stream")
				.header("Ocp-Apim-Subscription-Key", TOKENS.get("FaceAPI"))
				.POST(HttpRequest.BodyPublishers.ofFile(Paths.get(file.getPath())))
				.build();
		
		// Get HttpResponse from Face API
		HttpResponse<String> faceResp = client.send(faceReq, BodyHandlers.ofString());
		String json = faceResp.body();
		
		// Map JSON response using Jackson FasterXML library (built-in to Spring)
		ObjectMapper mapper = new ObjectMapper();
		JsonNode tree = mapper.readTree(json);								// Reads entire Json tree
		if (tree.size() == 0) {										// Make sure exactly one face has been detected in the image
			throw new FaceDoesNotExistException("No face was detected in the image.");
		} else if (tree.size() > 1) {
			throw new TooManyFacesException("Too many faces were detected in the image. The face limit is '1'.");
		}
		
		// Gets first face object (should be the only object) from JSON response and deserializes into Java Face object
		Face face = mapper.readValue(json, Face[].class)[0];
		
		// Check if glasses are already on the image
		if (!face.getFaceAttributes().getGlasses().equals("NoGlasses")) {
			throw new GlassesAlreadyOnException("Glasses were detected on the face image.");
		}
		
		// Check if face is straight
		Pose pose = face.getFaceAttributes().getHeadPose();
		Double pitchThreshold = 20.0;
		Double rollThreshold = 20.0;
		Double yawThreshold = 20.0;
		if (pose.getPitch() > pitchThreshold || pose.getPitch() < pitchThreshold * -1
				|| pose.getRoll() > rollThreshold || pose.getRoll() < rollThreshold * -1
				|| pose.getYaw() > yawThreshold || pose.getYaw() < yawThreshold * -1) {
			throw new FaceNotAlignedException("Face is not properly aligned. Current threshold is:\n"
																		   + "\tPitch: +-" + pitchThreshold.toString() + " degrees\n"
																		   + "\tRoll: +-" + rollThreshold.toString() + " degrees\n"
																		   + "\tYaw: +-" + yawThreshold.toString() + " degrees\n"
																		   + "Current values of submitted face are:\n"
																		   + "\tPitch: " + pose.getPitch() + " degree(s)\n"
																		   + "\tRoll: " + pose.getRoll() + " degree(s)\n"
																		   + "\tYaw: " + pose.getYaw() + " degree(s)\n");
		}
		
		face.setFaceId(face.getFaceId().replace("-", ""));				// remove dashes from face id (Thymeleaf can't read files with dashes)
		
		return face;
	}
	
	
	// storeBlob: 	Stores given file as a blob in Azure Cloud Storage account
	// inputs: 		File file = Java File object containing local path to the file being stored
	//						String filename = name of the file after it is stored
	// outputs: 	void
	public void storeBlob(File file, String filename) throws UncheckedIOException {
		
		// Using com.azure.blob package, create a blob container client that connects to the pre-configured face storage container
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.endpoint("https://gmstorage2022f.blob.core.windows.net")
				.sasToken(TOKENS.get("StorageAcc"))
				.containerName("face-storage")
				.buildClient();
		
		// Create a client for our blob and upload the blob file
		BlobClient client = containerClient.getBlobClient(filename);
		client.uploadFromFile(file.getPath());
		
		return;
	}
	
	
	// storeBlob: 	Retrieves and downloads a specified blob file in Azure Cloud Storage account
	// inputs: 		String filename = name of the file being obtained
	// outputs: 	void
	public String getBlob(String filename) throws FileAlreadyExistsException, UncheckedIOException {
		
		// Using com.azure.blob package, create a blob container client that connects to the pre-configured face storage container
		BlobContainerClient containerClient = new BlobContainerClientBuilder()
				.endpoint("https://gmstorage2022f.blob.core.windows.net")
				.sasToken(TOKENS.get("StorageAcc"))
				.containerName("face-storage")
				.buildClient();
		
		// Create a client for our blob and download the blob file
		BlobClient client = containerClient.getBlobClient(filename);
		String path = System.getProperty("user.dir") + "/temp/" + filename + ".jpg";
		client.downloadToFile(path);
		
		return path;
	}
	
	
	// scoreFace: Calls the deployed Azure Machine Learning service and scores a given image
	// inputs:		File file = Java File object containing the local path to the file (image) being scored
	// outputs:		Boolean = True if probability for 'good' label is higher, False if porbability for 'bad' label is highert
	public Boolean scoreFace(File file) throws URISyntaxException, IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		
		// Create HTTP request to consume Azure ML service
		HttpRequest scoreReq = HttpRequest.newBuilder()
				.uri(new URI("https://tempmodel2.eastus2.inference.ml.azure.com/score"))
				.header("Content-Type", "application/octet-stream")
				.header("Authorization", "Bearer " + TOKENS.get("MLService"))
				.POST(HttpRequest.BodyPublishers.ofFile(Paths.get(file.getPath())))
				.build();
		
		// Get HttpResponse from ML Service
		HttpResponse<String> scoreResp = client.send(scoreReq, BodyHandlers.ofString());
		String json = scoreResp.body();
		
		// Map JSON response using Jackson FasterXML library (built-in to Spring)
		ObjectMapper mapper = new ObjectMapper();
		JsonNode object = mapper.readTree(json);
		
		// Get probability values from mapped JSON response
		Double probBad = object.get("probs").get(0).asDouble();
		Double probGood = object.get("probs").get(1).asDouble();
		
		// Compare probabilities and return result (bad[0] == False; good[1] == True)
		if (probBad > probGood)
			return false;
		else
			return true;
	}
}
