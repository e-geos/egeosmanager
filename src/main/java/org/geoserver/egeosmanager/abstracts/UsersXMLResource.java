package org.geoserver.egeosmanager.abstracts;

import it.egeos.geoserver.utils.UsersManager;

import java.io.IOException;

import org.geoserver.rest.RestletException;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * UsersXMLResource is a (abstract) REST Resource for users/groups.
 * It only wraps the manager configuration: type and filename, so implements
 * getManager() and getFilename()
 */

public abstract class UsersXMLResource extends XMLResource<UsersManager> {
	protected static UsersManager manager=null;
	protected static String filename="usergroup/default/users.xml";
	
	public UsersXMLResource() {
		super();
	}
	
	public UsersXMLResource(Context context, Request request, Response response) {
		super(context, request, response);
	}
	
	protected UsersManager getManager(){
		try {
			if (manager==null)
				manager = new UsersManager(getFile(filename));
		}
		catch (IOException e) {
			throw new RestletException( "", Status.SERVER_ERROR_INTERNAL, e );
		}
		return manager;
	}
	
	protected String getFilename(){
		return filename;
	}
}

