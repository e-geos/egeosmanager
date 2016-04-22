package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.RolesXMLResource;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * UserRoleRefs is a REST Callable for manage users roles.
 * 
 */

public class UserRoleRefs extends RolesXMLResource {	
	public static String USERNAME_KEY="username";
	public static String ROLE_KEY="roleID"; 
	
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
	 * Returns the list of users rolerefs 
	 */
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getUserRoleRef();
	}
	
	/*
	 * Add a roleref with <username> and <roleid> in roles.xml
	 */
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String usr = params.get(REQUIRED).get(USERNAME_KEY);
		String role = params.get(REQUIRED).get(ROLE_KEY);
		manager.addRoleToUser(role,usr); //this force role creation
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	/*
	 * Remove a roleref with <username> and <roleid> in roles.xml
	 */
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String usr = params.get(REQUIRED).get(USERNAME_KEY);
		String role = params.get(REQUIRED).get(ROLE_KEY);
		manager.delRoleRefFromUser(role, usr);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(USERNAME_KEY);
			add(ROLE_KEY);
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
			add(USERNAME_KEY);
			add(ROLE_KEY);
		}};
	}

	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
