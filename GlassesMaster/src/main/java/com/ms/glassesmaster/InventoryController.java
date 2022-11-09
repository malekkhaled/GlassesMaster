package com.ms.glassesmaster;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ms.glassesmaster.azure.AzureService;
import com.ms.glassesmaster.exception.FaceDoesNotExistException;
import com.ms.glassesmaster.exception.FaceNotAlignedException;
import com.ms.glassesmaster.exception.GlassesAlreadyOnException;
import com.ms.glassesmaster.exception.TooManyFacesException;
import com.ms.glassesmaster.faces.Face;
import com.ms.glassesmaster.faces.FaceService;
import com.ms.glassesmaster.glasses.GlassesList;
import com.ms.glassesmaster.glasses.GlassesService;

@Controller
public class InventoryController {
	
	Logger log = LoggerFactory.getLogger(InventoryController.class);

	@Autowired
	private GlassesService glassesService;
	@Autowired
	private AzureService azureService;
	@Autowired
	private FaceService faceService;
	
	private static String TEMP_DIR = System.getProperty("user.dir") + "/temp/";
	
	
	@PostMapping("/inventory/detect")
	public String checkFaceImage(Model model, @RequestParam("image") MultipartFile mpFile, RedirectAttributes ra) throws IOException, InterruptedException, URISyntaxException {
		
		// Convert multiplart file to standard file and store locally with anonymized name, temporarily
		Random rand = new Random();
		Integer imgID = 100000 + rand.nextInt(900000);
		String fileExtension = '.' + mpFile.getContentType().substring(mpFile.getContentType().lastIndexOf('/') + 1);
		String filename = imgID.toString() + fileExtension;
		
		File file = new File(TEMP_DIR + filename);
		mpFile.transferTo(file);
		
		// Use Azure Face API to detect a face
		log.info("Attempting to detect face using Azure Face API...");
		Face face = new Face();
		try {
			face = azureService.detectFace(file);
		} catch (FaceDoesNotExistException e) {
			log.error("ERROR: " + e.getMessage() + " Returning to home page...");
			ra.addAttribute("faceExists", false);
			return "redirect:/home";
		} catch (TooManyFacesException e) {
			log.error("ERROR: " + e.getMessage() + " Returning to home page...");
			ra.addAttribute("multipleFace", true);
			return "redirect:/home";
		} catch (FaceNotAlignedException e) {
			log.error("ERROR: " + e.getMessage() + " Returning to home page...");
			ra.addAttribute("faceAligned", false);
			return "redirect:/home";
		} catch (GlassesAlreadyOnException e) {
			log.error("ERROR: " + e.getMessage() + " Returning to home page...");
			ra.addAttribute("glassesOn", true);
			return "redirect:/home";
		}
		
		// Store face image to Azure Blob Storage with faceId as file name and store object for retrieval later
		log.info("Storing face image in Azure Storage with ID " + face.getFaceId());
		azureService.storeBlob(file, face.getFaceId());
		faceService.insertFace(face);
		
		// Delete file locally
		file.delete();
		
		// Add attributes to be accessed by the form
		ra.addAttribute("faceId", face.getFaceId());
		
		return "redirect:/inventory";
	}
	
	
	@GetMapping("/inventory")
	public String resolveInventory(Model model, @ModelAttribute("faceId") String faceId, @RequestParam("hasSelected") Optional<Boolean> hasSelected) {
		
		log.info("Displaying inventory page...");
		
		// Add attributes to be accessed by the form
		if (hasSelected.isPresent()) {
			if (!hasSelected.get()) {
				log.info("Inventory page loaded with error message 'no glasses were selected'.");
				model.addAttribute("hasSelected", false);
			}
		}
		
		model.addAttribute("allGlasses", glassesService.getAllGlasses());
		model.addAttribute("faceId", faceId);
		model.addAttribute("selectedGlasses", new GlassesList());
				
		model.addAttribute("colors", glassesService.getColors());
		model.addAttribute("styles", glassesService.getStyles());
		model.addAttribute("brands", glassesService.getBrands());
				
		return "inventory";
	}
}
