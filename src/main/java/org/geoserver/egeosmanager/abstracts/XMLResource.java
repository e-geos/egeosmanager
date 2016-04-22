package org.geoserver.egeosmanager.abstracts;

import it.egeos.geoserver.utils.interfaces.Manager;

import java.io.FileNotFoundException;
import java.util.HashMap;

import org.geoserver.rest.RestletException;
import org.geoserver.rest.format.DataFormat;
import org.restlet.Context;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * XMLResource is a (abstract) REST Callable resource and get all commons methods
 * about real managers.
 * 
 * You need to implement if you extend:
 * 
 * - getManager(): returns the specific Manager (taken by egs-utils.jar)
 * 
 * - getFilaname(): returns the filename related the manager
 * 
 */

public abstract class XMLResource<T extends Manager> extends RemoteResource  {

	//file manager, static to share between all objs
	protected abstract T getManager();
	
	//filename of resource
	protected abstract String getFilename();
		
	/*
	 * Simple constructor with static manager creation
	 */
	public XMLResource() {
		super();
		getManager();
	}

	/*
	 * Simple constructor with static manager creation
	 */
	public XMLResource(Context context, Request request, Response response) {
		super(context, request, response);
		getManager();
	}

	/*
	 * Easy exception related a file not found
	 */
	protected Exception fileNotFoundException(){
		return new FileNotFoundException("file "+getFilename()+" not found");
	}

	/*
	 * Easy exception related a bad mediatype 
	 */
	protected RestletException badMediaType(Exception e){
		return new RestletException("MediaType found "+getRequest().getEntity().getMediaType(), Status.CLIENT_ERROR_UNSUPPORTED_MEDIA_TYPE, e);
	}
		
	/*
	 * Real handler for GET request: checks about manager 
	 */
	@Override
	public void handleGet() {		
		DataFormat format = getFormatGet();
		try {
			T man = getManager();
			if (man!=null){
				man.reload();
				getResponse().setEntity(format.toRepresentation(handleGetBody(format)));
			}
			else 
				throw fileNotFoundException();
		} 
		catch (Exception e) {
			throw new RestletException("handleGet", Status.SERVER_ERROR_INTERNAL, e );
		}
	}

	/*
	 * Real handler for POST requests: checks about manager and parameters
	 */
	@Override
	public void handlePost(){
		DataFormat format = getFormatPostOrPut();
		HashMap<String, HashMap<String, String>> params = parsePostForm();
		if (params.get(MISSING).size()==0)
			try {
				T man = getManager();
				if (man!=null){
					man.reload();
					handlePostBody(params, format);
				}
				else
					throw fileNotFoundException();
			} 
			catch (Exception e) {
				throw new RestletException("handlePost parameters:\n"+prettyfy(params), Status.SERVER_ERROR_INTERNAL, e );
			}
		else
			throw new RestletException("handleDelete parameters:\n"+prettyfy(params), Status.CLIENT_ERROR_BAD_REQUEST, new Exception("parameters are missing or invalid.") );
	}
	
	/*
	 * Real handler for DELETE requests: checks about manager and parameters
	 */	
	@Override
	public void handleDelete(){
		DataFormat format = getFormatPostOrPut();
		HashMap<String, HashMap<String, String>> params = parseDeleteForm();		
		if (params.get(MISSING).size()==0)
			try {
				T man = getManager();
				if (man!=null){
					man.reload();
					handleDeleteBody(params, format);
				}
				else
					throw fileNotFoundException();
			} 
			catch (Exception e) {
				throw new RestletException("handleDelete parameters:\n"+prettyfy(params), Status.SERVER_ERROR_INTERNAL, e );
			}
		else
			throw new RestletException("handleDelete parameters:\n"+prettyfy(params) , Status.CLIENT_ERROR_BAD_REQUEST, new Exception("parameters are missing or invalid.") );
	}	
}
