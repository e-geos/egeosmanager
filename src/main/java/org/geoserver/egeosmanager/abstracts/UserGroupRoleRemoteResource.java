package org.geoserver.egeosmanager.abstracts;

import java.io.IOException;

import org.geoserver.security.GeoServerRoleService;
import org.geoserver.security.GeoServerRoleStore;
import org.geoserver.security.GeoServerSecurityManager;
import org.geoserver.security.GeoServerUserGroupService;
import org.geoserver.security.GeoServerUserGroupStore;
import org.geoserver.security.impl.GeoServerUser;
import org.geoserver.security.impl.GeoServerUserGroup;
import org.geoserver.security.validation.PasswordPolicyException;
import org.geoserver.security.validation.RoleStoreValidationWrapper;
import org.geoserver.security.validation.UserGroupStoreValidationWrapper;

public abstract class UserGroupRoleRemoteResource extends RemoteResource {
	protected GeoServerSecurityManager authenticationManager;
	
	public UserGroupRoleRemoteResource(GeoServerSecurityManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	protected GeoServerUserGroupService loadUserGroupService() throws IOException{				
		return authenticationManager.loadUserGroupService("default");
	}
		
	protected GeoServerUserGroupService loadUserGroupService(String name) throws IOException{
		return authenticationManager.loadUserGroupService(name);
	}
	
	protected GeoServerUserGroupStore getStore(GeoServerUserGroupService ugs) throws IOException{
		return new UserGroupStoreValidationWrapper(ugs.createStore());
	}	
	
	protected GeoServerRoleService loadRoleService() throws IOException{				
		return authenticationManager.loadRoleService("default");
	}
		
	protected GeoServerRoleService loadRoleService(String name) throws IOException{
		return authenticationManager.loadRoleService(name);
	}
	
	protected GeoServerRoleStore getStore(GeoServerRoleService urs) throws IOException{
		return new RoleStoreValidationWrapper(urs.createStore());
	}
	
	protected GeoServerUserGroupStore getStore() throws IOException{
		return getStore(loadUserGroupService());		
	}
	
	protected GeoServerUser createUserObjectAndStore(GeoServerUserGroupStore store,String login, String pwd) throws IOException, PasswordPolicyException{				
		GeoServerUser user = store.createUserObject(login, pwd, true);
		store.addUser(user);
		store.store();	
		return user;
	}
	
	protected GeoServerUserGroup createGroupObjectAndStore(GeoServerUserGroupStore store,String name) throws IOException{
		GeoServerUserGroup group = store.createGroupObject(name, true);
		store.addGroup(group);
		store.store();			
		return group;
	}
}
