package com.ms.glassesmaster;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping({"/home", "/"})
public class HomepageController {
	
	Logger log = LoggerFactory.getLogger(HomepageController.class);
	
	@RequestMapping(method = RequestMethod.GET)
	// Displays the home page
	public String displayForm(Model model, 
			@RequestParam Optional<Boolean> faceExists, @RequestParam Optional<Boolean> multipleFace, 
			@RequestParam Optional<Boolean> faceAligned, @RequestParam Optional<Boolean> glassesOn) {
		
		log.info("Resolving homepage...");
		
		if (faceExists.isPresent()) {
			if (!faceExists.get()) {
				log.info("Homepage loaded with error message 'face does not exist'.");
				model.addAttribute("faceExists", false);
			}
		}
		
		if (multipleFace.isPresent()) {
			if (multipleFace.get()) {
				log.info("Homepage loaded with error message 'too many faces'.");
				model.addAttribute("multipleFace", true);
			}
		}
		
		if (faceAligned.isPresent()) {
			if (!faceAligned.get()) {
				log.info("Homepage loaded with error message 'face is not aligned'.");
				model.addAttribute("faceAligned", false);
			}
		}
		
		if (glassesOn.isPresent()) {
			if (glassesOn.get()) {
				log.info("Homepage loaded with error message 'face is already wearing glasses'.");
				model.addAttribute("glassesOn", true);
			}
		}
		
		return "home";
	}
}
