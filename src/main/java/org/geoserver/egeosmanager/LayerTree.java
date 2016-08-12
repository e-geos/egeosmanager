package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.geoserver.catalog.Catalog;
import org.geoserver.catalog.CoverageInfo;
import org.geoserver.catalog.CoverageStoreInfo;
import org.geoserver.catalog.DataStoreInfo;
import org.geoserver.catalog.FeatureTypeInfo;
import org.geoserver.catalog.LayerGroupInfo;
import org.geoserver.catalog.WMSLayerInfo;
import org.geoserver.catalog.WMSStoreInfo;
import org.geoserver.catalog.WorkspaceInfo;
import org.geoserver.egeosmanager.abstracts.RemoteResource;
import org.geoserver.rest.format.DataFormat;
import org.geotools.geometry.jts.ReferencedEnvelope;

public class LayerTree extends RemoteResource{		
	private Catalog catalog;
		
	public LayerTree(Catalog catalog){
		this.catalog=catalog;
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
	protected Object handleGetBody(DataFormat format) throws Exception {
		return new JSONObject(){{
			put("layers",new JSONObject(){{
				put("featureTypes",new JSONArray(){{
					for (FeatureTypeInfo l:catalog.getFeatureTypes())						
						put(layer2json(l));										
				}});
				
				put("coverages",new JSONArray(){{
					for(CoverageInfo l:catalog.getCoverages())
						put(layer2json(l));
				}});
				
				put("wmsLayers",new JSONArray(){{					
					for(WMSLayerInfo l:catalog.getResources(WMSLayerInfo.class))
						put(layer2json(l));
				}});
				
				put("layergroups",new JSONArray(){{								
					for(LayerGroupInfo l:catalog.getLayerGroups())
						put(layer2json(l));
				}});
			}});			
		}};
	}

	@Override
	protected void handlePostBody(HashMap<String, HashMap<String, String>> params, DataFormat format) throws Exception {
		//This method is forbidden		
	}

	@Override
	protected void handleDeleteBody(HashMap<String, HashMap<String, String>> params, DataFormat format)throws Exception {
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

	private JSONObject workspace2json(final WorkspaceInfo wsi) throws JSONException{
		return new JSONObject(){{
			try {
				put("name",wsi.getName());
			} 
			catch (NullPointerException e) {
				//Layergroups may have no workspace
			}			
		}};
	}
	
	private JSONObject store2json(final DataStoreInfo si) throws JSONException{
		return new JSONObject(){{
			put("@class","dataStore");
			put("name",si.getName());			
		}};
	}

	private JSONObject store2json(final CoverageStoreInfo si) throws JSONException{
		return new JSONObject(){{
			put("@class","coverageStore");
			put("name",si.getName());
		}};
	}

	private JSONObject store2json(final WMSStoreInfo si) throws JSONException{
		return new JSONObject(){{
			put("@class","wmsStore");
			put("name",si.getName());
		}};
	}
	 
	private JSONObject bbox2json(final ReferencedEnvelope nbb) throws JSONException{
		return new JSONObject(){{
			put("minx",nbb.getMinX());
			put("maxx",nbb.getMaxX());
			put("miny",nbb.getMinY());
			put("maxy",nbb.getMaxY());
			put("crs",new JSONObject(){{
				put("@class","projected");
				put("$",nbb.getCoordinateReferenceSystem().getName());
			}});
		}};
	}
	
	private JSONObject layer2json(final FeatureTypeInfo l) throws JSONException{
		return new JSONObject(){{
			put("name",l.getName());
			put("nativeName", l.getNativeName());
			String t = l.getTitle();
			put("title",t!=null?t:l.getName());			
			put("srs",l.getSRS());
			put("nativeBoundingBox",bbox2json(l.getNativeBoundingBox()));
			put("store",store2json(l.getStore()));	
			put("workspace",workspace2json(l.getStore().getWorkspace()));
		}};
	}
	
	private JSONObject layer2json(final CoverageInfo l) throws JSONException{
		return new JSONObject(){{
			put("name",l.getName());
			put("nativeName", l.getNativeName());
			String t = l.getTitle();
			put("title",t!=null?t:l.getName());			
			put("srs",l.getSRS());
			put("nativeBoundingBox",bbox2json(l.getNativeBoundingBox()));
			put("store",store2json(l.getStore()));		
			put("workspace",workspace2json(l.getStore().getWorkspace()));
		}};
	}
	
	private JSONObject layer2json(final WMSLayerInfo l) throws JSONException{
		return new JSONObject(){{
			put("name",l.getName());
			put("nativeName", l.getNativeName());
			String t = l.getTitle();
			put("title",t!=null?t:l.getName());			
			put("srs",l.getSRS());
			put("nativeBoundingBox",bbox2json(l.getNativeBoundingBox()));
			put("store",store2json(l.getStore()));	
			put("workspace",workspace2json(l.getStore().getWorkspace()));
		}};
	}
	
	private JSONObject layer2json(final LayerGroupInfo l) throws JSONException{
		return new JSONObject(){{
			put("name",l.getName());
			put("nativeName", l.getName());
			String t = l.getTitle();
			put("title",t!=null?t:l.getName());			
			put("srs",l.getBounds().getCoordinateReferenceSystem().getName());
			put("nativeBoundingBox",bbox2json(l.getBounds()));
			put("store",new JSONObject());
			put("workspace",workspace2json(l.getWorkspace()));
		}};
	}
}
