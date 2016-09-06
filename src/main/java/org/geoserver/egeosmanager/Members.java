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
 * Members is a REST Callable for manage users/groups relations.
 * 
 */
@Help(text="This method allow to manage users/groups relation on Geoserver.")
public class Members extends UsersXMLResource {	
	public static String USER_KEY="username";
	public static String GROUP_KEY="groupname";
	public static String UNIQUE="force_unique";
	
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
	 * Returns the list of users/groups 
	 */
	@Help(
		text="Returns a JSON object with group names as keys and a list of users as value."		
	)
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getMembers();
	}
	
	/*
	 * Assign a <username> to a <groupname> in users.xml
	 */
	@Help(
		text="Add a user to a group, optionally you can specify if avoid duplication.",
		requires = {
			@Parameter(name="username",description="the name of the user you want to join in a group."),
			@Parameter(name="groupname",description="the name of the group you want to add to a user."),
		},
		optionals = {
			@Parameter(name="force_unique",description="force to avoid duplication."),
		}
	)		
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(USER_KEY);
		String group = params.get(REQUIRED).get(GROUP_KEY);
		String unique= params.get(OPTIONAL).get(UNIQUE);
		manager.addMember(login, group,Boolean.parseBoolean(unique));
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove assignment a <username> to a <groupname> in users.xml
	 */
	@Help(
		text="Remove a user from a group.",
		requires = {
			@Parameter(name="username",description="the name of the user you want to remove from a group."),
			@Parameter(name="groupname",description="the name of the group you want to remove from a user."),
		}
	)		
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(USER_KEY);
		String group = params.get(REQUIRED).get(GROUP_KEY);
		manager.delMember(login, group);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(USER_KEY);
			add(GROUP_KEY);
		}};
	}
	

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTOpt() {
		return new ArrayList<String>(){{
			add(UNIQUE);
		}};
	}
	

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEReq() {	
		return new ArrayList<String>(){{
			add(USER_KEY);
			add(GROUP_KEY);
		}};
	}
	

	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
