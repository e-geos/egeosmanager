package org.geoserver.egeosmanager.rest;

public class EgeosRestMapping extends org.geoserver.rest.RESTMapping{
	public int getSize(){
		return getRoutes().size();
	}
}
