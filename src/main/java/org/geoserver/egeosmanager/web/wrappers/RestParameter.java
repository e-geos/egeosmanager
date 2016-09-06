package org.geoserver.egeosmanager.web.wrappers;

import java.io.Serializable;

public class RestParameter implements Serializable{
	private static final long serialVersionUID = -2171542921819245860L;
	private String name;
	private String description;

	public RestParameter() {
		super();
	}

	public RestParameter(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}	
}
