package org.beryx.viewreka.parameter;

import org.beryx.viewreka.core.ViewrekaException;

public class Attribute {
	private final String key;
	private final String value;
	
	public Attribute(String entry) {
		int pos = entry.indexOf(':');
		if(pos <= 0) throw new ViewrekaException("Invalid attribute entry: " + entry);
		this.key = entry.substring(0, pos).trim();
		this.value = entry.substring(pos + 1).trim();
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}
}
