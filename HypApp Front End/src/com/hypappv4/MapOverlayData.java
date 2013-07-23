package com.hypappv4;

public class MapOverlayData {
	public float[][] data;
	public float scale;
	public String type;
	
	public MapOverlayData (float newScale, float[][]newData, String newType){
		data = newData;
		scale = newScale;
		type = newType;
	}
	
	public float getScale (){
		return scale;
	}
	
	public float[][] getData (){
		return data;
	}
	
	public String getType (){
		return type;
	}
}
