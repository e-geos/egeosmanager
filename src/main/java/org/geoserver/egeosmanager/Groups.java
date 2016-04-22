package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UsersXMLResource;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Groups is a REST Resource for groups.
 * 
 */

public class Groups extends UsersXMLResource {
	public static String NAME_KEY="name";
	
	/*
	 * Enable POST
	 */
	@Override
	public boolean allowPost(){
		return true;
	}

	/*
	 * Enable DELETE
	 */
	@Override
	public boolean allowDelete(){
		return true;
	}

	/*
	 * Returns the list of groups 
	 */
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getGroups();
	}
	
	/*
	 * Add a group with <name> users.xml
	 */
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String name = params.get(REQUIRED).get(NAME_KEY);
		manager.addGroup(name);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a group with <name> users.xml
	 */
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String name = params.get(REQUIRED).get(NAME_KEY);
		manager.delGroup(name);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(NAME_KEY);		
		}};
	}

	@Override
	protected ArrayList<String> getPOSTOpt() {
		return null;
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEReq() {
		return new ArrayList<String>(){{
			add(NAME_KEY);
		}};
	}
	

	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
