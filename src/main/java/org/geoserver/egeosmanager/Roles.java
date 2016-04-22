package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.RolesXMLResource;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Roles is a REST Callable for manage roles.
 * 
 */

public class Roles extends RolesXMLResource {	
	public static String ID_KEY="id";
	public static String PARENT_KEY="parent_id"; 

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
	 * Returns the list of roles
	 */
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getRoles();
	}
	
	/*
	 * Add a role with <id> (and optionally a <parent>) in roles.xml
	 */
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String id = params.get(REQUIRED).get(ID_KEY);
		String parent = params.get(OPTIONAL).get(PARENT_KEY);
		manager.addRole(id,parent);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a role with <id> in roles.xml
	 */
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String id = params.get(REQUIRED).get(ID_KEY);
		manager.delRole(id);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(ID_KEY);
		}};
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTOpt() {
		return new ArrayList<String>(){{
			add(PARENT_KEY);
		}};	
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEReq() {
		return new ArrayList<String>(){{
			add(ID_KEY);
		}};
	}

	
	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
