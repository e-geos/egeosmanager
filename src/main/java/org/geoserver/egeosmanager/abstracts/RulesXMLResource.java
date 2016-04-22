package org.geoserver.egeosmanager.abstracts;

import it.egeos.geoserver.utils.RulesManager;

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
 * RulesXMLResource is a (abstract) REST Resource for rules.
 * It only wraps the manager configuration: type and filename, so implements
 * getManager() and getFilename()
 */

public abstract class RulesXMLResource extends XMLResource<RulesManager> {
	protected static RulesManager manager=null;
	protected static String filename="layers.properties";

	public RulesXMLResource() {
		super();
	}
	
	public RulesXMLResource(Context context, Request request, Response response) {
		super(context, request, response);
	}
	
	protected RulesManager getManager(){
		try {
			if (manager==null)
				manager = new RulesManager(getFile(filename));
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
