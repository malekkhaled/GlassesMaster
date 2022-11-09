package com.ms.glassesmaster.results;

import java.util.List;

public class ResultPOJO {
	
	private List<Result> results;
	
	public ResultPOJO() {}

	public ResultPOJO(List<Result> results) {
		super();
		this.results = results;
	}

	public List<Result> getResults() {
		return results;
	}
	public void setResults(List<Result> results) {
		this.results = results;
	}
}
