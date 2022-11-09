package com.ms.glassesmaster.glasses;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/glasses")
public class GlassesRestController {

	private final GlassesService glassesService = new GlassesService();
	
	@GetMapping
	public List<Glasses> getAllGlasses() {
		return glassesService.getAllGlasses();
	}
	
	@GetMapping("/{id}")
	public Glasses getGlassesByID(@RequestParam String id) {
		return glassesService.getGlassesByID(id);
	}
}
