package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UsersXMLResource;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Users is a REST Callable for manage users.
 * 
 */

public class Users extends UsersXMLResource {	
	public static String LOGIN_KEY="login";
	public static String PASSWORD_KEY="password";
	
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
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getUsers();
	}
	
	/*
	 * Add a user with <login> and <password> in users.xml
	 */
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(LOGIN_KEY);
		String pwd = params.get(REQUIRED).get(PASSWORD_KEY);
		manager.addUser(login, pwd);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a user from in users.xml
	 */
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(LOGIN_KEY);
		manager.delUser(login);
		manager.save();
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
