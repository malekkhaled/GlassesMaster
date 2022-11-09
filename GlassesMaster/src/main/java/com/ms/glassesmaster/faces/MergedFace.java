package com.ms.glassesmaster.faces;

import com.ms.glassesmaster.glasses.Glasses;

public class MergedFace {

	private String id;
	private Face face;
	private Glasses glasses;
	
	// Constructor
	public MergedFace(String id, Face face, Glasses glasses) {
		super();
		this.id = id;
		this.face = face;
		this.glasses = glasses;
	}

	// Getters/Setters
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public Face getFace() {
		return face;
	}
	public void setFace(Face face) {
		this.face = face;
	}

	public Glasses getGlasses() {
		return glasses;
	}
	public void setGlasses(Glasses glasses) {
		this.glasses = glasses;
	}
}
