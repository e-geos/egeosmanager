package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.geoserver.egeosmanager.abstracts.RemoteResource;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.rest.format.DataFormat;
import org.geowebcache.grid.BoundingBox;
import org.geowebcache.grid.Grid;
import org.geowebcache.grid.GridSet;
import org.geowebcache.grid.GridSetBroker;

/**
 * 
 * @author Federico C. Guizzardi - cippinofg <at> gmail.com
 * 
 * Gridsets is a REST Callable to get all gridsets
 * 
 */

@Help(text="This method allow to access at Geoserver gridsets.")
public class Gridsets extends RemoteResource{
	private GridSetBroker gwc_gs_broker;
	
	public Gridsets(GridSetBroker gwc_gs_broker) {
		super();
		this.gwc_gs_broker = gwc_gs_broker;
	}

	@Override
	public boolean allowPost() {
		return false;
	}

	@Override
	public boolean allowDelete() {
		return false;
	}
	
	@Override
	@Help(text="Returns a JSON array with all gridsets defined on geoserver.")
	protected Object handleGetBody(DataFormat format) throws Exception {
		return new JSONObject(){{
			put("gridsets",new JSONArray(){{
				for(final GridSet g:gwc_gs_broker.getGridSets())
					put(new JSONObject(){{
						put("name",g.getName());
						put("description",g.getDescription());
						put("srs",g.getSrs());
						put("bounds",new JSONObject(){{
							BoundingBox b = g.getBounds();
							put("minx",b.getMinX());
							put("miny",b.getMinX());
							put("maxx",b.getMaxX());
							put("maxy",b.getMaxY());
						}});						
						put("tileWidth",g.getTileWidth());
						put("tileHeight",g.getTileHeight());						
						put("metersPerUnit",g.getMetersPerUnit());
						put("pixelSize",g.getPixelSize());						
						put("levels",new JSONArray(){{
							for(int i=0;i<g.getNumLevels();i++){
								final Grid gr = g.getGrid(i);
								put(new JSONObject(){{							
									 put("name",gr.getName());								 
									 put("resolution",gr.getResolution());
									 put("numTilesHigh",gr.getNumTilesHigh());
									 put("numTilesWide",gr.getNumTilesWide());
									 put("scaleDenominator",gr.getScaleDenominator());									
								}});						
							}
						}});
					}});
			}});
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
