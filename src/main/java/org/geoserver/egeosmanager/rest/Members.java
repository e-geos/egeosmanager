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
import org.geoserver.security.impl.GeoServerUserGroup;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Members is a REST Callable for manage users/groups relations.
 * 
 */
@Help(text="This method allow to manage users/groups relation on Geoserver.")
public class Members extends UserGroupRoleRemoteResource {	
	public static String USER_KEY="username";
	public static String GROUP_KEY="groupname";
	public static String UNIQUE="force_unique";

	public Members(GeoServerSecurityManager authenticationManager) {
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
	 * Returns the list of users/groups 
	 */
	@Help(text="Returns a JSON object with group names as keys and a list of users as value.")
	protected Object handleGetBody(DataFormat format) throws Exception{
		return new JSONObject(){{
			for (String ugServiceName : authenticationManager.listUserGroupServices()) {
                SecurityUserGroupServiceConfig config = authenticationManager.loadUserGroupServiceConfig(ugServiceName);                      
                final GeoServerUserGroupService ugs = loadUserGroupService(config.getName());                                        
            	for(final GeoServerUserGroup g:ugs.getUserGroups())
            		put(g.getGroupname(),new JSONArray(){{
            			for(GeoServerUser u:ugs.getUsersForGroup(g))
            				put(u.getUsername());
            		}});
    		}
		}};
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
			@Parameter(name="force_unique",description="delete all other users."),
		}
	)		
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String login = params.get(REQUIRED).get(USER_KEY);
		String group = params.get(REQUIRED).get(GROUP_KEY);
		String unique= params.get(OPTIONAL).get(UNIQUE);
				
		GeoServerUserGroupStore store = getStore();
		GeoServerUser u = store.getUserByUsername(login);
				
		GeoServerUserGroup g = store.getGroupByGroupname(group);
		if (g==null)
			g=createGroupObjectAndStore(store,group);
		
		if (unique!=null && Boolean.parseBoolean(unique))
			for(GeoServerUser s:store.getUsersForGroup(g))
				store.disAssociateUserFromGroup(s, g);

		store.associateUserToGroup(u, g);
		store.store();
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
		
		GeoServerUserGroupStore store = getStore();
		store.disAssociateUserFromGroup(store.getUserByUsername(login), store.getGroupByGroupname(group));
		store.store();
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
