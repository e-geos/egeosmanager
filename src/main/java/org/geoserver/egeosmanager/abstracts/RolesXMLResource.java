package org.geoserver.egeosmanager.abstracts;

import it.egeos.geoserver.utils.RolesManager;

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
 * RolesXMLResource is a (abstract) REST Resource for users/groups roles.
 * It only wraps the manager configuration: type and filename, so implements
 * getManager() and getFilename()
*/

public abstract class RolesXMLResource extends XMLResource<RolesManager> {
	protected static RolesManager manager=null;
	protected static String filename="role/default/roles.xml";
	
	public RolesXMLResource() {
		super();
	}

	public RolesXMLResource(Context context, Request request, Response response) {
		super(context, request, response);
	}
		
	protected RolesManager getManager(){
		try {
			if (manager==null)
				manager = new RolesManager(getFile(filename));
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
