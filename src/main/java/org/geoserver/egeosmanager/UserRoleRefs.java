package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.RolesXMLResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * UserRoleRefs is a REST Callable for manage users roles.
 * 
 */
@Help(text="This method allow to manage users/roles relation on Geoserver.")
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
	@Help(
		text="Returns a JSON object with user names as keys and a list of roles as value."		
	)
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getUserRoleRef();
	}
	
	/*
	 * Add a roleref with <username> and <roleid> in roles.xml
	 */
	@Help(
		text="Add a user to a role.",
		requires = {
			@Parameter(name="username",description="the name of the user you want to link with a role."),
			@Parameter(name="roleID",description="the name of the role you want to add to a user."),
		}
	)	
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
	@Help(
		text="Remove a role from a user.",
		requires = {
			@Parameter(name="username",description="the name of the user you want to unlink with a role."),
			@Parameter(name="roleID",description="the name of the role you want to remove from a user."),
		}
	)
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
