package com.buddha.simulation;

import java.util.HashMap;

public class Properties {
	
	public static final String MIN_PREFIX = "MIN_";
	public static final String MAX_PREFIX = "MAX_";
	
	public HashMap<String, Integer> integerProperties = new HashMap<String, Integer>();
	public HashMap<String, Float> floatProperties = new HashMap<String, Float>();
	
	public Properties() {
		
	}
	
	public void setIProperty(String name, int defaultValue, int min, int max) {
		integerProperties.put(name, defaultValue);
		integerProperties.put(MIN_PREFIX+name, min);
		integerProperties.put(MAX_PREFIX+name, max);
	}
	
	public int getIMin(String name) {
		return integerProperties.get(MIN_PREFIX+name);
	}
	
	public int getIMax(String name) {
		return integerProperties.get(MAX_PREFIX+name);
	}
	
	public void setIProperty(String name, int value) {
		integerProperties.put(name, value);
	}
	
	public int getI(String name) {
		return integerProperties.get(name);
	}
	
	public void setFProperty(String name, float defaultValue, float min, float max) {
		floatProperties.put(name, defaultValue);
		floatProperties.put(MIN_PREFIX+name, min);
		floatProperties.put(MAX_PREFIX+name, max);
	}
	
	public float getFMin(String name) {
		return floatProperties.get(MIN_PREFIX+name);
	}
	
	public float getFMax(String name) { 
		return floatProperties.get(MAX_PREFIX+name);
	}
	
	public float getF(String name) {
		return floatProperties.get(name);
	}
	
	public void setFProperty(String name, float value) {
		floatProperties.put(name, value);
	}
	
}
