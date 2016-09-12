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
import org.geoserver.security.impl.GeoServerUserGroup;
import org.json.JSONArray;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Groups is a REST Resource for groups.
 * 
 */
@Help(text="This method allow to manage groups on Geoserver.")
public class Groups extends UserGroupRoleRemoteResource {
	public static String NAME_KEY="name";
	
	public Groups(GeoServerSecurityManager authenticationManager) {
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
	 * Returns the list of groups 
	 */
	@Help(text="Returns a JSON array of group names.")
	protected Object handleGetBody(DataFormat format) throws Exception{
		return new JSONArray(){{
        	for (String ugServiceName : authenticationManager.listUserGroupServices()) {
                SecurityUserGroupServiceConfig config = authenticationManager.loadUserGroupServiceConfig(ugServiceName);                      
                GeoServerUserGroupService ugs = loadUserGroupService(config.getName());                                        
            	for(final GeoServerUserGroup g:ugs.getUserGroups())
            		put(g.getGroupname());
    		}
        }};
	}
	
	/*
	 * Add a group with <name> users.xml
	 */
	@Help(
		text="Creates a new group on Geoserver.",
		requires = {
			@Parameter(name="name",description="the name of the group you want to create on geoserver."),
		}
	)	
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String name = params.get(REQUIRED).get(NAME_KEY);
	
		createGroupObjectAndStore(getStore(),name);
		
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a group with <name> users.xml
	 */
	@Help(
		text="Removes a group on Geoserver.",
		requires = {
			@Parameter(name="name",description="the group name you want to delete on geoserver."),
		}
	)	
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String name = params.get(REQUIRED).get(NAME_KEY);
		
		GeoServerUserGroupStore store = getStore();
		
		GeoServerUserGroup g = store.getGroupByGroupname(name);
		if (g!=null){
			store.removeGroup(g);
			store.store();
		}
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(NAME_KEY);		
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
			add(NAME_KEY);
		}};
	}

	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
