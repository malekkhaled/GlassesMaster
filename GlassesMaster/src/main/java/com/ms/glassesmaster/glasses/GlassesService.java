package com.ms.glassesmaster.glasses;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;

@Service
public class GlassesService {

	// Environment variables read Glasses objects in from .csv file
	private static String CSV_DIR = System.getProperty("user.dir") + "/src/main/resources/data/glasses_table.csv";
	private final List<Glasses> glassesList = readInGlasses();
	
	
	// Returns a list of all glasses
	public List<Glasses> getAllGlasses() {
		return glassesList;
	}
	
	
	// Returns a list of all glasses with a given color
	public List<Glasses> getGlassesByColor(String color) {
		List<Glasses> results = new ArrayList<Glasses>();
		for (Glasses eyeglass : glassesList) {
			if (eyeglass.getColor().compareTo(color) == 0)
				results.add(eyeglass);
		}
		return results;
	}
	
	
	// Returns an eyeglass given its name
	public Glasses getGlassesByID(String id) {
		return glassesList.stream().filter(g -> g.getGlassesID().equals(id)).findFirst().get();
	}
	
	
	// Returns a set of unique colors among all Glasses in the object list
	public Set<String> getColors() {
		Set<String> colors = new TreeSet<>();
		for (Glasses g : glassesList) {
			colors.add(g.getColor());
		}
		
		return colors;
	}
	
	// Returns a set of unique styles among all Glasses in the object list
	public Set<String> getStyles() {
		Set<String> styles = new TreeSet<>();
		for (Glasses g : glassesList) {
			styles.add(g.getStyle());
		}
			
		return styles;
	}
	
	// Returns a set of unique colors among all Glasses in the object list
	public Set<String> getBrands() {
		Set<String> brands = new TreeSet<>();
		for (Glasses g : glassesList) {
			brands.add(g.getBrand());
		}
			
		return brands;
	}
	
	
	// Function that reads from the .csv file (uses opencsv library)
	private List<Glasses> readInGlasses() {
		List<Glasses> glasses = new ArrayList<Glasses>();
		try {
			glasses = new CsvToBeanBuilder<Glasses>(new FileReader(CSV_DIR))
					.withType(Glasses.class)
					.build()
					.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return glasses;
	}
}
