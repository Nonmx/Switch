package sselab.switchy.property;

public class Parameter {
	private String name;
	private String minVal;
	private String maxVal;
	public Parameter(String name, String minVal, String maxVal) {
		this.name = name;
		this.minVal = minVal;
		this.maxVal = maxVal;
	}
	
	public String getName(){
		return name;
	}
	
	public String getMinVal(){
		return minVal;
	}
	
	public String getMaxVal(){
		return maxVal;
	}

}
