package com.ms.glassesmaster.glasses;

import java.util.List;

// Object required for Thymeleaf processing
public class GlassesList {

	private List<String> glasses;
	
	// Constructor
	public GlassesList() {}
	
	// Getters/Setters
	public List<String> getGlasses() {
		return glasses;
	}
	public void setGlasses(List<String> glasses) {
		this.glasses = glasses;
	}
	
}
