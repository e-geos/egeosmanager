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
 * Users is a REST Callable for manage users.
 * 
 */
@Help(text="This method allow to manage users on Geoserver.")
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
	@Help(
		text="Returns a JSON array of user names."		
	)
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getUsers();
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
		manager.addUser(login, pwd);
		manager.save();
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
