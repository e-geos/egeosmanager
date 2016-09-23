package org.geoserver.egeosmanager.rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.egeosmanager.abstracts.RemoteResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.rest.format.DataFormat;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * StyleTree is a REST Callable for see all styles
 * 
 */
@Help(text="This method allow to see all styles on Geoserver.")
public class StylesTree extends RemoteResource{
	private Catalog catalog;
	
	public StylesTree(Catalog catalog) {
		super();
		this.catalog = catalog;
	}

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}
	
	@Help(text="Returns a JSON object with all styles divided by workspace, if applicable.")
	@Override
	protected Object handleGetBody(DataFormat format) throws Exception {
		return new JSONObject(){{
			put("",new JSONArray(){{
				for(StyleInfo s:catalog.getStyles())
					put(s.getName());				
			}});
								
			for(WorkspaceInfo w:catalog.getWorkspaces()){
				final List<StyleInfo> styles = catalog.getStylesByWorkspace(w);
				if (styles!=null && !styles.isEmpty())
					put(w.getName(),new JSONArray(){{
						for(StyleInfo s:styles)
							put(s.getName());
					}});				
			}
		}};
	}

	@Override
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params, DataFormat format) throws Exception {
		//This method is forbidden
	}

	@Override
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params, DataFormat format) throws Exception {
		//This method is forbidden		
	}
	
	@Override
	protected ArrayList<String> getPOSTReq() {
		return null;
	}

	@Override
	protected ArrayList<String> getPOSTOpt() {
		return null;
	}

	@Override
	protected ArrayList<String> getDELETEReq() {
		return null;
	}

	@Override
	protected ArrayList<String> getDELETEOpt() {
		return null;
	}
}
