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
 * GroupRoleRefs is a REST Resource for groups roles.
 * 
 */
@Help(text="This method allow to manage groups/roles relation on Geoserver.")
public class GroupRoleRefs extends RolesXMLResource {	
	public static String GROUPNAME_KEY="groupname";
	public static String ROLE_KEY="roleID"; 
	
	/*
	 * Enable Post requests
	 */
	@Override
	public boolean allowPost(){
		return true;
	}

	/*
	 * Enable Delete requests
	 */
	@Override
	public boolean allowDelete(){
		return true;
	}

	/*
	 * Returns the list of group rolerefs
	 */
	@Help(
		text="Returns a JSON object with group names as keys and a list of roles as value."		
	)
	protected Object handleGetBody(DataFormat format) throws Exception{
		return manager.getGroupRoleRef();
	}
	
	/*
	 * Add a roleref with <groupname> and <roleid> in roles.xml
	 */
	@Help(
		text="Add a group to a role.",
		requires = {
			@Parameter(name="groupname",description="the name of the group you want to link with a role."),
			@Parameter(name="roleID",description="the name of the role you want to add to a group."),
		}
	)		
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String grp = params.get(REQUIRED).get(GROUPNAME_KEY);
		String role = params.get(REQUIRED).get(ROLE_KEY);
		manager.addRoleToGroup(role, grp); //this force role creation
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a roleref with <groupname> and <roleid> in roles.xml
	 */
	@Help(
		text="Remove a role from a group.",
		requires = {
			@Parameter(name="groupname",description="the name of the group you want to unlink with a role."),
			@Parameter(name="roleID",description="the name of the role you want to remove from a group."),
		}
	)	
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String grp = params.get(REQUIRED).get(GROUPNAME_KEY);
		String role = params.get(REQUIRED).get(ROLE_KEY);
		manager.delRoleRefFromGroup(role,grp);
		manager.save();
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(GROUPNAME_KEY);
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
			add(GROUPNAME_KEY);
			add(ROLE_KEY);
		}};
	}
	
	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
