package org.geoserver.egeosmanager;

import java.util.ArrayList;
import java.util.HashMap;

import org.geoserver.egeosmanager.abstracts.RemoteResource;
import org.geoserver.rest.format.DataFormat;

public class Layers extends RemoteResource{
	
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
		//under development: this method produces a reload of the feature
		return null;
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
}
