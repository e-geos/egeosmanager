package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UsersXMLResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Groups is a REST Resource for groups.
 * 
 */
@Help(text="This method allow to manage groups on Geoserver.")
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
	@Help(
		text="Returns a JSON array of group names."		
	)
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getGroups();
	}
	
	/*
	 * Add a group with <name> users.xml
	 */
	@Help(
		text="Creates a new group on Geoserver.",
		requires = {
			@Parameter(name="name",description="the name of the group you want to create on geoserver."),
		}
	)	
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String name = params.get(REQUIRED).get(NAME_KEY);
		manager.addGroup(name);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a group with <name> users.xml
	 */
	@Help(
		text="Removes a group on Geoserver.",
		requires = {
			@Parameter(name="name",description="the group name you want to delete on geoserver."),
		}
	)	
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
