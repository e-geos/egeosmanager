package org.geoserver.egeosmanager.web.wrappers;

import java.io.Serializable;
import java.util.List;

public class RestCall implements Serializable{
	private static final long serialVersionUID = 7018158294778418616L;

	private String description;
	private List<RestParameter> required;
	private List<RestParameter> optional;
	
	public RestCall() {
		super();
	}

	public RestCall(String description, List<RestParameter> required, List<RestParameter> optional) {
		super();
		this.description = description;
		this.required = required;
		this.optional = optional;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<RestParameter> getRequired() {
		return required;
	}

	public void setRequired(List<RestParameter> required) {
		this.required = required;
	}

	public List<RestParameter> getOptional() {
		return optional;
	}

	public void setOptional(List<RestParameter> optional) {
		this.optional = optional;
	}
}
