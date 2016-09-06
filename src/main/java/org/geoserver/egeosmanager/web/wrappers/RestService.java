package org.geoserver.egeosmanager.web.wrappers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.geoserver.egeosmanager.web.providers.RestServiceProvider;

public class RestService implements Serializable{
	private static final long serialVersionUID = 6431674084861027778L;
	
	private String name;
	private String url;
	private String base;
	private String description;
	private RestCall helpGet;
	private RestCall helpPost;
	private RestCall helpDelete;

	public RestService() {
		super();
	}

	public RestService(String name, String url, String base, String description, RestCall helpGet, RestCall helpPost,
			RestCall helpDelete) {
		super();
		this.name = name;
		this.url = url;
		this.base = base;
		this.description = description;
		this.helpGet = helpGet;
		this.helpPost = helpPost;
		this.helpDelete = helpDelete;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public String getAbsoluteUrl(){
		return base+url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public RestCall getHelpGet() {
		return helpGet;
	}

	public void setHelpGet(RestCall helpGet) {
		this.helpGet = helpGet;
	}

	public RestCall getHelpPost() {
		return helpPost;
	}

	public void setHelpPost(RestCall helpPost) {
		this.helpPost = helpPost;
	}

	public RestCall getHelpDelete() {
		return helpDelete;
	}

	public void setHelpDelete(RestCall helpDelete) {
		this.helpDelete = helpDelete;
	}
	
	public List<RestParameter> getRequired(String call){
		if (RestServiceProvider.HELPGET.getName().equals(call) && helpGet!=null)
			return helpGet.getRequired();
		else if (RestServiceProvider.HELPPOST.getName().equals(call) && helpPost!=null)
			return helpPost.getRequired();
		else if (RestServiceProvider.HELPDELETE.getName().equals(call) && helpDelete!=null)
			return helpDelete.getRequired();
		else 
			return new ArrayList<RestParameter>();
	}

	public List<RestParameter> getOptinal(String call){
		if (RestServiceProvider.HELPGET.getName().equals(call) && helpGet!=null)
			return helpGet.getOptional();
		else if (RestServiceProvider.HELPPOST.getName().equals(call) && helpPost!=null)
			return helpPost.getOptional();
		else if (RestServiceProvider.HELPDELETE.getName().equals(call) && helpDelete!=null)
			return helpDelete.getOptional();
		else 
			return new ArrayList<RestParameter>();
	}
}
