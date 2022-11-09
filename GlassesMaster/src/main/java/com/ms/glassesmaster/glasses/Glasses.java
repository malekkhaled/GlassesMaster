package com.ms.glassesmaster.glasses;

import com.opencsv.bean.CsvBindByName;

public class Glasses {

	@CsvBindByName(column = "ID")
	private String glassesID;
	@CsvBindByName(column = "Color")
	private String color;
	@CsvBindByName(column = "Brand")
	private String brand;
	@CsvBindByName(column = "Style")
	private String style;
	
	// Constructors
	public Glasses() {}
	
	public Glasses(String glassesID, String color, String brand, String style) {
		super();
		this.glassesID = glassesID;
		this.color = color;
		this.brand = brand;
		this.style = style;
	}

	// Getters/Setters
	public String getGlassesID() {
		return glassesID;
	}
	public void setGlassesID(String glassesID) {
		this.glassesID = glassesID;
	}

	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}

	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
	// TODO: Helper function to get the image from Azure Cloud Storage using the ID
	
}
