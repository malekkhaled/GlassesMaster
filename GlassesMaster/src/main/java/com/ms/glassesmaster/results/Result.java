package com.ms.glassesmaster.results;

public class Result {
	
	private String mergedImageID;
	private Boolean result;			// 0(bad) == False; 1(good) = True
	
	// Constructors
	public Result() {}
	
	public Result(String mergedImageID, Boolean result) {
		super();
		this.mergedImageID = mergedImageID;
		this.result = result;
	}

	// Getters/Setters
	public String getMergedImageID() {
		return mergedImageID;
	}
	public void setMergedImageID(String mergedImageID) {
		this.mergedImageID = mergedImageID;
	}
	
	public Boolean getResult() {
		return result;
	}
	public void setResult(Boolean result) {
		this.result = result;
	}
}
