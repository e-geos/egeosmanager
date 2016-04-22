package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UsersXMLResource;
import org.geoserver.rest.format.DataFormat;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Members is a REST Callable for manage users/groups relations.
 * 
 */

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
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getMembers();
	}
	
	/*
	 * Assign a <username> to a <groupname> in users.xml
	 */
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
