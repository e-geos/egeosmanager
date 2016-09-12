package org.geoserver.egeosmanager.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.geoserver.egeosmanager.abstracts.RemoteResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.security.AccessMode;
import org.geoserver.security.impl.DataAccessRule;
import org.geoserver.security.impl.DataAccessRuleDAO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.restlet.data.Status;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Rules is a REST Callable for manage rules.
 * 
 */
@Help(text="This method allow to manage rules on Geoserver.")
public class Rules extends RemoteResource{
	public static final String WORKSPACE_KEY="workspace";
	public static final String LAYER_KEY="layer";
	public static final String METHOD_KEY="method";
	public static final String ROLE_KEY="role";
	public static final String MISSING_KEY="missing";
	public static final String APPEND_KEY="append";
	
	public static final char ADMIN='a'; 
	public static final char READ='r';
	public static final char WRITE='w';
	
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
	 * Returns the list of rules 
	 */
	@Help(
		text="Returns a JSON object with rule paths as keys and a list of roles as value."		
	)	
	protected Object handleGetBody(DataFormat format) throws Exception{
		return new JSONObject(){{
			DataAccessRuleDAO rulesDao =DataAccessRuleDAO.get();
			for(final DataAccessRule r:rulesDao.getRules())
				put(r.getKey(),new JSONArray(){{
					for(String ru:r.getRoles())
						put(ru);
				}});
		}};
	}
	
	/*
	 * Try to create a rule in layers.default or add all roles to an existing rule 
	 */
	@Help(
		text="Add a role to a rule if exists, otherwise creates a new rule.",
		requires = {
			@Parameter(name="workspace",description="the name of the workspace."),
			@Parameter(name="method",description="the name of the method (r,w,a)."),
			@Parameter(name="role",description="the name of the role you want to add to the rule."),
		},
		optionals = {			
			@Parameter(name="append",description="this flag avoid a creation of a new rule when no one is found."),
			@Parameter(name="layer",description="the name of the layer of the rule."),
		}
	)		
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String ws = params.get(REQUIRED).get(WORKSPACE_KEY);
		String md = params.get(REQUIRED).get(METHOD_KEY);
		String rl = params.get(REQUIRED).get(ROLE_KEY);
		String ap = params.get(OPTIONAL).get(APPEND_KEY);
		String ly = params.get(OPTIONAL).get(LAYER_KEY);
		
		if (ly==null || ly.trim().isEmpty())
			ly="*";
		
		DataAccessRuleDAO rulesDao =DataAccessRuleDAO.get();
		Set<String> roles = new HashSet<String>(Arrays.asList(rl.split(",")));
		
		AccessMode accessMode;
		switch (md.charAt(0)) {
		case ADMIN:
			accessMode=AccessMode.ADMIN;
			break;
		case READ:
			accessMode=AccessMode.READ;
			break;
		case WRITE:
			accessMode=AccessMode.WRITE;
			break;
		default:
			throw new Exception("Bad Access Mode '"+md+"': it should be "+ADMIN+" or "+WRITE+" or "+READ);
		}
		
		DataAccessRule rule=new DataAccessRule(ws,ly, accessMode, roles);
		DataAccessRule found =null;
		
		for(final DataAccessRule r:rulesDao.getRules())
			found = r.equals(rule)?r:found;
				
		String res="ok";
		if (ap!=null && ap.trim().equalsIgnoreCase("true")){
			if (found!=null)
				found.getRoles().addAll(roles);
			else{
				res="Rule '"+rule.getKey()+"' not found";
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,res);				
			}				
		}
		else if (found!=null)
			found.getRoles().addAll(roles);
		else
			rulesDao.addRule(rule);
					
		rulesDao.storeRules();
		
		getResponse().setEntity(format.toRepresentation(res));
	}
	
	/*
	 * Try to remove a rule or a relation between a rule and a list of roles  
	 */
	@Help(
		text="Try to remove a rule or a relation between a rule and a list of roles.",
		requires = {
			@Parameter(name="workspace",description="the name of the workspace."),
			@Parameter(name="method",description="the name of the method (r,w,a)."),			
		},
		optionals = {			
			@Parameter(name="role",description="the name of the role you want to remove from the rule."),					
			@Parameter(name="missing",description="this flag allow to ignore if the rule you want to delete doesn't exist."),
			@Parameter(name="layer",description="the name of the layer of the rule."),
		}
	)	
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params,DataFormat format) throws Exception{
		String ws = params.get(REQUIRED).get(WORKSPACE_KEY);
		String md = params.get(REQUIRED).get(METHOD_KEY);
		String rl = params.get(OPTIONAL).get(ROLE_KEY);
		String ms = params.get(OPTIONAL).get(MISSING_KEY);
		String ly = params.get(OPTIONAL).get(LAYER_KEY);
		
		if (ly==null || ly.trim().isEmpty())
			ly="*";
		
		DataAccessRuleDAO rulesDao =DataAccessRuleDAO.get();
		
		AccessMode accessMode;
		switch (md.charAt(0)) {
		case ADMIN:
			accessMode=AccessMode.ADMIN;
			break;
		case READ:
			accessMode=AccessMode.READ;
			break;
		case WRITE:
			accessMode=AccessMode.WRITE;
			break;
		default:
			throw new Exception("Bad Access Mode '"+md+"': it should be "+ADMIN+" or "+WRITE+" or "+READ);
		}
		
		DataAccessRule rule=new DataAccessRule(ws,ly, accessMode);
		DataAccessRule found =null;
		
		for(final DataAccessRule r:rulesDao.getRules())
			found = r.equals(rule)?r:found;
		
		String res="ok";
		if (found!=null){
			if (rl!=null && rl.length()>0)
				found.getRoles().removeAll(new HashSet<String>(Arrays.asList(rl.split(","))));			
			else
				rulesDao.removeRule(found);
		}
		else{
			res="Rule '"+rule.getKey()+"' not found";				
			if (ms==null || !ms.trim().equalsIgnoreCase("true"))
				getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST,res);				
		}
				
		rulesDao.storeRules();
		getResponse().setEntity(format.toRepresentation(res));
	}
	
	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTReq() {
		return new ArrayList<String>(){{
			add(WORKSPACE_KEY);			
			add(METHOD_KEY);
			add(ROLE_KEY);
		}};
	}

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getPOSTOpt() {
		return new ArrayList<String>(){{
			add(APPEND_KEY);
			add(LAYER_KEY);
		}};
	}

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEReq() {
		return new ArrayList<String>(){{
			add(WORKSPACE_KEY);
			add(METHOD_KEY);
		}};
	}

	@SuppressWarnings("serial")
	@Override
	protected ArrayList<String> getDELETEOpt() {
		return new ArrayList<String>(){{
			add(ROLE_KEY);
			add(MISSING_KEY);
			add(LAYER_KEY);
		}};
	}
}
