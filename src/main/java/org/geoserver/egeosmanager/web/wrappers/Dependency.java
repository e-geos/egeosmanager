package org.geoserver.egeosmanager.web.wrappers;

import java.io.Serializable;

public class Dependency implements Serializable{
	private static final long serialVersionUID = 2290809445915460487L;
	private String name="fake";
	private String version=null;
	private String author=null;
	private Boolean found=false;
		
	public Dependency() {
		super();	
	}

	public Dependency(Package pkg) {
		super();
		this.found= pkg!=null;
		
		if (found){
			name=pkg.getImplementationTitle();
			if (name==null || name.length()<1)
				name=pkg.getName();
			
			version=pkg.getImplementationVersion();			
			author=pkg.getImplementationVendor();
		}		
		else
			name=pkg.getName();
	}
	
	public Dependency(String name){
		this.name = name;
		this.found = false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Boolean getFound() {
		return found;
	}

	public void setFound(Boolean found) {
		this.found = found;
	}	
}
