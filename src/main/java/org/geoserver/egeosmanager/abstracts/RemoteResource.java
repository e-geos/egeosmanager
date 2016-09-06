package org.geoserver.egeosmanager.abstracts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.geoserver.config.GeoServerDataDirectory;
import org.geoserver.platform.GeoServerExtensions;
import org.geoserver.platform.GeoServerResourceLoader;
import org.geoserver.rest.AbstractResource;
import org.geoserver.rest.RestletException;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.rest.format.StringFormat;
import org.geotools.util.logging.Logging;
import org.restlet.Context;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
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
 * - getPOSTReq(): returns a list of required parameters when a POST request is done
 * 
 * - getPOSTOpt(): returns a list of optional parameters when a POST request is done
 * 
 * - getDELETEReq(): returns a list of required parameters when a DELETE request is done
 * 
 * - getDELETEOpt(): returns a list of optional parameters when a DELETE request is done
 * 
 * - handleGetBody(): this method is what is called when a GET request is done, all checks are made before
 * 
 * - handlePostBody(): this method is what is called when a POST request is done, all checks are made before (in handlePost)
 *  
 * - handleDeleteBody(): this method is what is called when a DELETE request is done, all checks are made before (in handleDelete)
 * 
 */

public abstract class RemoteResource extends AbstractResource {
	protected static String MISSING="missing";
	protected static String REQUIRED="required";
	protected static String OPTIONAL="optional";
	protected static String FORM="form";
		
	static final Logger log = Logging.getLogger(RemoteResource.class);
	
	public RemoteResource() {
		super();
	}

	public RemoteResource(Context context, Request request, Response response) {
		super(context, request, response);
	}

	//necessary inputs from post
	protected abstract ArrayList<String> getPOSTReq();
	
	//optional inputs from post
	protected abstract ArrayList<String> getPOSTOpt();
	
	//necessary inputs from delete
	protected abstract ArrayList<String> getDELETEReq();
	
	//optional inputs from delete
	protected abstract ArrayList<String> getDELETEOpt();
		
	//Actions made after a GET is received
	//checks made before (in handleGet): manager presence
	protected abstract Object handleGetBody(DataFormat format) throws Exception;
	
	//Actions made after a PORT is received
	//checks made before (in handlePost): presence and vality of parameter, manager presence
	protected abstract void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception;
	
	//Actions made after a DELETE is received
	//checks made before (in handleDelete): presence and vality of parameter, manager presence
	protected abstract void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception;
	
	/*
	 * Format available: only JSon is available
	 */
	@SuppressWarnings("serial")
	@Override
	protected List<DataFormat> createSupportedFormats(Request request,Response response) {
		return new ArrayList<DataFormat>(){{
			add(new StringFormat(MediaType.APPLICATION_JSON));
		}};
	}

	/*
	 * Override of getFormatGet to add a fallback
	 */
	@Override
	protected DataFormat getFormatGet() {
		DataFormat format=null;
		try {
			format = super.getFormatGet();
		} 
		catch (Exception e) {
			format=new StringFormat(MediaType.APPLICATION_JSON);
		}
		return format;
	}

	/*
	 * Override of getFormatPostOrPut to add a fallback
	 */
	@Override
	protected DataFormat getFormatPostOrPut() {
		DataFormat format=null;
		try {
			format = super.getFormatPostOrPut(); 
		} 
		catch (Exception e) {
			format=new StringFormat(MediaType.APPLICATION_JSON);
		}
		return format;
	}
	
	/*
	 * Parser from request: it produces a hashmap with required, optional, missing and form parameters.
	 * Required are all parameters in getPOSTReq() list: they are necessary to act the request;
	 * Optional are all parameters in getPOSTOpt() list: they are not necessary, so any use needs a test of presence;
	 * Missing are all parameters in getPOSTReq() list that are not in request;
	 * Form are all parameters in request, useful for debug
	 */
	@SuppressWarnings("serial")
	protected HashMap<String,HashMap<String,String>> parsePostForm(){
		ArrayList<String> req = getPOSTReq();
		ArrayList<String> opt = getPOSTOpt();
		
		HashMap<String, HashMap<String,String>> res = new HashMap<String,HashMap<String,String>>(){{
			put(MISSING,new HashMap<String,String>());
			put(REQUIRED,new HashMap<String,String>());
			put(OPTIONAL,new HashMap<String,String>());
			put(FORM,new HashMap<String,String>());
		}};
		Form ff = getRequest().getEntityAsForm();
		for(String k:ff.getNames())
			res.get(FORM).put(k,ff.getFirstValue(k));
		
		String v;
		if(req!=null)
			for(String k:req){				
				v = ff.getFirstValue(k);
				if(v!=null && !v.isEmpty())
					res.get(REQUIRED).put(k,v);
				else
					res.get(MISSING).put(k,null);
			}			
		if(opt!=null)
			for(String k:opt){
				v = ff.getFirstValue(k);
				if(v!=null && !v.isEmpty())
					res.get(OPTIONAL).put(k,v);
			}					
		return res;
	}

	/*
	 * Parser from request: it produces a hashmap with required, optional, missing and form parameters.
	 * Required are all parameters in getDELETEReq() list: they are necessary to act the request;
	 * Optional are all parameters in getDELETEOpt() list: they are not necessary, so any use needs a test of presence;
	 * Missing are all parameters in getDELETEReq() list that are not in request;
	 * Form are all parameters in request, useful for debug
	 */
	@SuppressWarnings("serial")
	protected HashMap<String,HashMap<String,String>> parseDeleteForm(){
		ArrayList<String> req = getDELETEReq();
		ArrayList<String> opt = getDELETEOpt();

		HashMap<String, HashMap<String,String>> res = new HashMap<String,HashMap<String,String>>(){{
			put(MISSING,new HashMap<String,String>());
			put(REQUIRED,new HashMap<String,String>());
			put(OPTIONAL,new HashMap<String,String>());
			put(FORM,new HashMap<String,String>());
		}};
		
		Map<String, Object> att = getRequest().getAttributes();
		Map<String, String> qp = parseQuery((String)att.get("q"));
		for(String k:qp.keySet())
			res.get(FORM).put(k,qp.get(k));
		
		if(req!=null)
			for(String k:req){
				String v=qp.get(k);
				if(v!=null && !v.isEmpty())
					res.get(REQUIRED).put(k,v);
				else
					res.get(MISSING).put(k,null);						
			}			
		if(opt!=null)
			for(String k:opt){
				String v = qp.get(k);
				if(v!=null && !v.isEmpty())
					res.get(OPTIONAL).put(k,v);
			}					
		return res;
	}
	
	/*
	 * Produces a hashmap from a query string
	 */
	@SuppressWarnings("serial")
	protected HashMap<String,String> parseQuery(final String s){	
		return new HashMap<String, String>(){{
			for (String r:s.split("&")){
				try{			
					String[] kv=r.split("=");					
					String v=Reference.decode(kv[1]);
					if(v.matches("\\[.*\\]"))					
						v=v.length()>2?v.substring(1, v.length()-1):null;						
					put(kv[0],v);				
				}
				catch (Exception e){
					//can't parse parameters so nothing is added					
				}
			}
		}};
	}

	/*
	 * Returns a printable version of a hashmap, for debug
	 */
	protected String prettyfy(HashMap<String, HashMap<String, String>> params){
		String s="";
		s+="\t"+REQUIRED+"\n";
		for(String k:params.get(REQUIRED).keySet())
			s+="\t\t "+k+":"+params.get(REQUIRED).get(k)+"\n";
		
		s+="\t"+MISSING+"\n";
		for(String k:params.get(MISSING).keySet())
			s+="\t\t "+k+":null\n";
		
		s+="\t"+OPTIONAL+"\n";
		for(String k:params.get(OPTIONAL).keySet())
			s+="\t\t "+k+":"+params.get(OPTIONAL).get(k)+"\n";

		s+="\t"+FORM+"\n";
		for(String k:params.get(FORM).keySet())
			s+="\t\t "+k+":"+params.get(FORM).get(k)+"\n";
		return s;
	}
	
	/*
	 * Returns the full path of the file fn
	 */
	protected String getFile(String fn) throws IOException{
		GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
		GeoServerDataDirectory dd = new GeoServerDataDirectory(loader);		
		return dd.getSecurity(fn).file().getCanonicalPath();
	}

	/*
	 * Returns the full path of a workspace 
	 */
	protected String getWorkspacePath(String workspace) throws IOException{
		GeoServerResourceLoader loader = GeoServerExtensions.bean(GeoServerResourceLoader.class);
		GeoServerDataDirectory dd = new GeoServerDataDirectory(loader);
		return dd.getWorkspaces(workspace).dir().getAbsolutePath();
	}
		
	/*
	 * Real handler for GET request: checks about manager 
	 */
	@Override
	public void handleGet() {		
		DataFormat format = getFormatGet();
		try {
			getResponse().setEntity(format.toRepresentation(handleGetBody(format)));
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
				handlePostBody(params, format);
			} 
			catch (Exception e) {
				throw new RestletException("handlePost parameters:\n"+prettyfy(params), Status.SERVER_ERROR_INTERNAL, e );
			}
		else
			throw new RestletException("handlePost parameters:\n"+prettyfy(params), Status.CLIENT_ERROR_BAD_REQUEST, new Exception("parameters are missing or invalid.") );
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
				handleDeleteBody(params, format);
			} 
			catch (Exception e) {
				throw new RestletException("handleDelete parameters:\n"+prettyfy(params), Status.SERVER_ERROR_INTERNAL, e );
			}
		else
			throw new RestletException("handleDelete parameters:\n"+prettyfy(params) , Status.CLIENT_ERROR_BAD_REQUEST, new Exception("parameters are missing or invalid.") );
	}
}
