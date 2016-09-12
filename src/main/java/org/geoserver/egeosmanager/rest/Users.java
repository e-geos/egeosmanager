package org.geoserver.egeosmanager.rest;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UserGroupRoleRemoteResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.GeoServerUserGroupStore;
import org.geoserver.security.config.SecurityUserGroupServiceConfig;
import org.geoserver.security.impl.GeoServerUser;
import org.json.JSONArray;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Users is a REST Callable for manage users.
 * 
 */
@Help(text="This method allow to manage users on Geoserver.")
public class Users extends UserGroupRoleRemoteResource {			
	public static String LOGIN_KEY="login";
	public static String PASSWORD_KEY="password";
	
	public Users(GeoServerSecurityManager authenticationManager) {
		super(authenticationManager);
	}

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
	 * Returns the list of users 
	 */
	@Help(text="Returns a JSON array of user names.")
	protected Object handleGetBody(DataFormat format) throws Exception{
        return new JSONArray(){{
        	for (String ugServiceName : authenticationManager.listUserGroupServices()) {
                SecurityUserGroupServiceConfig config = authenticationManager.loadUserGroupServiceConfig(ugServiceName);                      
                GeoServerUserGroupService ugs = loadUserGroupService(config.getName());                                        
            	for(final GeoServerUser u:ugs.getUsers())
            		put(u.getUsername());
    		}
        }};
	}
	
	/*
	 * Add a user with <login> and <password> in users.xml
	 */
	@Help(
		text="Creates a new user on Geoserver.",
		requires = {
			@Parameter(name="login",description="a new username you want to create on geoserver."),
			@Parameter(name="password",description="a password related username."),
		}
	)
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(LOGIN_KEY);
		String pwd = params.get(REQUIRED).get(PASSWORD_KEY);	
		createUserObjectAndStore(getStore(),login, pwd);
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a user from in users.xml
	 */
	@Help(
		text="Removes a user from Geoserver.",
		requires = {
			@Parameter(name="login",description="a username you want to delete on geoserver."),
		}
	)
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(LOGIN_KEY);
		
		GeoServerUserGroupStore store = getStore();
		
		GeoServerUser u = store.getUserByUsername(login);
		if (u!=null){
			store.removeUser(u);
			store.store();
		}
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(LOGIN_KEY);
			add(PASSWORD_KEY);
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
			add(LOGIN_KEY);
		}};
	}
	
	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
