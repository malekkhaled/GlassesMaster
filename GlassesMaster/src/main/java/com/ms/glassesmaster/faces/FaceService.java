package com.ms.glassesmaster.faces;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.springframework.stereotype.Service;

@Service
public class FaceService {
	
	private Map<String, Face> faceInfo = new HashMap<String, Face>();
	
	
	public void insertFace(Face face) {
		faceInfo.put(face.getFaceId(), face);
		return;
	}
	
	public Face getFace(String faceId) {
		return faceInfo.get(faceId);
	}
	
	public void deleteFace(String faceId) {
		faceInfo.remove(faceId);
		return;
	}

	
	// mergeImage:		Algorithm to overlay an image of glasses on an image of a face
	// inputs:				Face face = Face object containing information of the face as well as the name of the image to be fetched
	//								String faceImagePath = path of the locally stored face image
	//								String glassesId = name of the glasses object to be fetched
	// outputs:				File = the resulting file created if the operation was successful
	public File mergeImage(Face face, String faceImagePath, String glassesId) throws IOException {
		
		String glassesImagePath = System.getProperty("user.dir") + "/src/main/resources/static/images/glasses/" + glassesId + ".png";
		
		// Create BufferedImage streams to be able to draw the images
		BufferedImage faceImage = ImageIO.read(new File(faceImagePath));
		BufferedImage glassesImage = ImageIO.read(new File(glassesImagePath));
		BufferedImage mergedImage = new BufferedImage(faceImage.getWidth(), faceImage.getHeight(), BufferedImage.TYPE_INT_RGB);
		
		// Calculate amount to scale glasses
		int faceWidth = face.getFaceRectangle().getWidth();
		int glassesWidth = glassesImage.getWidth();
		int glassesHeight = glassesImage.getHeight();
		double scale = ((double)faceWidth / (double)glassesWidth);
		
		// Resize and rotate glasses (uses imgscalr library to cut down boilerplate code)
		BufferedImage resizedGlasses = Scalr.resize(glassesImage, (int)(glassesWidth * scale), (int)(glassesHeight * scale));
		double rotation = Math.toRadians(face.getFaceAttributes().getHeadPose().getRoll());
		AffineTransform transform = AffineTransform.getRotateInstance(rotation, resizedGlasses.getWidth() / 2, resizedGlasses.getHeight() / 2 );
		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
		
		// Calculate the x and y coordinates to draw the glasses
		int centerX = (int)((face.getFaceLandmarks().getPupilRight().getX() + face.getFaceLandmarks().getPupilLeft().getX()) / 2);
		int glassesX = centerX - (resizedGlasses.getWidth() / 2);
		int centerY = (int)((face.getFaceLandmarks().getPupilRight().getY() + face.getFaceLandmarks().getPupilLeft().getY()) / 2);
		int glassesY = centerY - (resizedGlasses.getHeight() / 2);
		
		// Draw images on new merged image
		Graphics g = mergedImage.getGraphics();
		g.drawImage(faceImage, 0, 0, null);
		g.drawImage(op.filter(resizedGlasses, null), glassesX, glassesY, null);
		g.dispose();
		
		// Write new image
		try {
			File file = new File(System.getProperty("user.dir") + "/temp/" + face.getFaceId() + '_' + glassesId + ".jpg");
			ImageIO.write(mergedImage, "JPG", file);
			return file;
		} catch (IOException e) {
			return null;
		}
	}  // end mergeImage
}
