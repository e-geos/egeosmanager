package org.geoserver.egeosmanager.rest;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.UserGroupRoleRemoteResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerRoleStore;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.config.SecurityRoleServiceConfig;
import org.geoserver.security.impl.GeoServerRole;
import org.json.JSONArray;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Roles is a REST Callable for manage roles.
 * 
 */
@Help(text="This method allow to manage roles on Geoserver.")
public class Roles extends UserGroupRoleRemoteResource {	
	public static String ID_KEY="id";
	public static String PARENT_KEY="parent_id"; 

	public Roles(GeoServerSecurityManager authenticationManager) {
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
	 * Returns the list of roles
	 */
	@Help(text="Returns a JSON array of role names.")	
	protected Object handleGetBody(DataFormat format) throws Exception{
        return new JSONArray(){{
        	for (String ugServiceName : authenticationManager.listRoleServices()) {
                SecurityRoleServiceConfig config = authenticationManager.loadRoleServiceConfig(ugServiceName);                      
                GeoServerRoleService ugs = loadRoleService(config.getName());                                        
            	for(final GeoServerRole u:ugs.getRoles())
            		put(u.getAuthority());
    		}
        }};
	}
	
	/*
	 * Add a role with <id> (and optionally a <parent>) in roles.xml
	 */
	@Help(
		text="Creates a new role on Geoserver.",
		requires = {
			@Parameter(name="id",description="the name of the role you want to create on geoserver."),
		},
		optionals = {
			@Parameter(name="parent_id",description="the name of the parent role, if exists.")
		}
	)		
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String id = params.get(REQUIRED).get(ID_KEY);
		String parent = params.get(OPTIONAL).get(PARENT_KEY);

		GeoServerRoleService ugs = loadRoleService();
		GeoServerRoleStore store = getStore(ugs);
		
		GeoServerRole role = ugs.createRoleObject(id);
		store.addRole(role);
		if (parent!=null)
			store.setParentRole(role,store.getRoleByName(parent));	

		store.store();			
		getResponse().setEntity(format.toRepresentation("ok"));
	}
	
	/*
	 * Remove a role with <id> in roles.xml
	 */
	@Help(
		text="Remove a role on Geoserver.",
		requires = {
			@Parameter(name="id",description="the name of the role you want to remove from geoserver."),
		}
	)	
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String id = params.get(REQUIRED).get(ID_KEY);

		GeoServerRoleService ugs = loadRoleService();
		GeoServerRoleStore store = getStore(ugs);
		
		GeoServerRole u = store.getRoleByName(id);
		if (u!=null){
			store.removeRole(u);
			store.store();
		}
		
		getResponse().setEntity(format.toRepresentation("ok"));
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(ID_KEY);
		}};
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTOpt() {
		return new ArrayList<String>(){{
			add(PARENT_KEY);
		}};	
	}

	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEReq() {
		return new ArrayList<String>(){{
			add(ID_KEY);
		}};
	}

	
	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
