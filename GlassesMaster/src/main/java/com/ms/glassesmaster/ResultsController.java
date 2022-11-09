package com.ms.glassesmaster;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ms.glassesmaster.azure.AzureService;
import com.ms.glassesmaster.faces.Face;
import com.ms.glassesmaster.faces.FaceService;
import com.ms.glassesmaster.glasses.GlassesList;
import com.ms.glassesmaster.results.Result;
import com.ms.glassesmaster.results.ResultPOJO;

@Controller
@RequestMapping("/results")
public class ResultsController {
	
	Logger log = LoggerFactory.getLogger(ResultsController.class);

	@Autowired
	private AzureService azureService;
	@Autowired
	private FaceService faceService;
	
	
	@RequestMapping(method = RequestMethod.POST)
	public String resolveResults(Model model, @ModelAttribute("selectedGlasses") GlassesList selectedGlasses,
			@ModelAttribute("faceId") String faceId, RedirectAttributes ra) {
		
		if (selectedGlasses.getGlasses().isEmpty()) {
			ra.addAttribute("hasSelected", false);
			ra.addAttribute("faceId", faceId);
			return "redirect:/inventory";
		}
		
		List<Result> results = new ArrayList<Result>();
		
		// Get selected glasses IDs
		List<String> glasses = selectedGlasses.getGlasses();
		Face face = faceService.getFace(faceId);
		String faceImagePath = null;
		
		log.info("Glasses have been selected for user " + faceId);
		
		// Attempt to retrieve the face image
		try {
			faceImagePath = azureService.getBlob(face.getFaceId());
		} catch (FileAlreadyExistsException e1) {
			// TODO Error handling
			log.warn(faceImagePath + " already exists on local server.");
		} catch (UncheckedIOException e2) {
			// TODO Error handling
		}
		
		// Loop through each selected glasses and merge the image with the face, storing the result in Azure Cloud Storage
		for (String id : glasses) {
			try {
				File file = faceService.mergeImage(face, faceImagePath, id);
				String mergedFaceId = face.getFaceId() + '_' + id;
				try {
					azureService.storeBlob(file, mergedFaceId);
				} catch (Exception e) {
					// File already exists, continue with code
					log.info("Image with ID '" + mergedFaceId + "' already exists in Azure Storage. Continuing regular operations...");
				}
				results.add(new Result(mergedFaceId, azureService.scoreFace(file)));
				file.delete();
			} catch (Exception e) {
				
			}
		}
		
		// Delete file from local storage
		new File(faceImagePath).delete();
		
		ra.addAttribute("index", 0);
		ra.addFlashAttribute("results", results);
		return "redirect:/results/show";
	}
	
	
	@PostMapping("/index")
	public String indexResults(@RequestParam Integer index, @RequestParam String scroll, @ModelAttribute("pojo") ResultPOJO pojo, RedirectAttributes ra) {
		
		// Delete previous image from local storage
		List<Result> results = pojo.getResults();
		Result result = results.get(index);
		String filename = result.getMergedImageID();
		String path = System.getProperty("user.dir") + "/temp/" + filename + ".jpg";
		new File(path).delete();
		
		// Increment or decrement index based on input from form
		if (scroll.compareTo("next") == 0)
			index++;
		else if (scroll.compareTo("prev") == 0)
			index--;
		
		ra.addAttribute("index", index);
		ra.addFlashAttribute("results", results);
		return "redirect:/results/show";
	}
	
	
	@GetMapping("/show")
	public String displayResult(Model model, @RequestParam Integer index, @ModelAttribute("results") List<Result> results) {
		
		// Get Result object from list
		Result result = results.get(index);
		
		// Retrieve result image from Azure Storage
		try {
			azureService.getBlob(result.getMergedImageID());
		} catch (Exception e) {
			// TODO Error handling
		}
		
		// Initialize empty list for Thymeleaf to populate and pass
		List<Result> resultList = new ArrayList<Result>(results.size());
		while (resultList.size() < results.size())
			resultList.add(new Result());
		ResultPOJO pojo = new ResultPOJO();
		pojo.setResults(resultList);
		
		log.info("Displaying result at index " + index.toString() + " with image ID of " + result.getMergedImageID());

		model.addAttribute("index", index);
		model.addAttribute("maxIndex", results.size() - 1);
		model.addAttribute("result", result);
		model.addAttribute("resultsList", results);
		model.addAttribute("pojo", pojo);
		return "results";
	}
	
	
	@PostMapping("/return")
	public String returnToInventory(@ModelAttribute("pojo") ResultPOJO pojo, RedirectAttributes ra) {
		List<Result> results = pojo.getResults();
		String mergedImageID = results.get(0).getMergedImageID();
		String faceId = mergedImageID.substring(0, mergedImageID.indexOf('_'));
		
		log.info("User with face ID '" + faceId + "' returning to /inventory page.");
		
		ra.addAttribute("faceId", faceId);
		return "redirect:/inventory";
	}
}
