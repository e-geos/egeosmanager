package org.geoserver.egeosmanager.web.providers;

import java.util.Arrays;
import java.util.List;

import org.geoserver.egeosmanager.web.wrappers.RestService;
import org.geoserver.web.wicket.GeoServerDataProvider;

public class RestServiceProvider extends GeoServerDataProvider<RestService>{
	private static final long serialVersionUID = 3655051570286742835L;

	@SuppressWarnings("serial")
	public static final Property<RestService> NAME = new AbstractProperty<RestService>("name"){
		@Override
		public String getPropertyValue(RestService item) {
			return item.getName();
		}		
	};
	
	@SuppressWarnings("serial")
	public static final Property<RestService> URL = new AbstractProperty<RestService>("url"){
		@Override
		public String getPropertyValue(RestService item) {
			return item.getUrl();
		}		
	};

	@SuppressWarnings("serial")
	public static final Property<RestService> DESCRIPTION = new AbstractProperty<RestService>("description"){
		@Override
		public String getPropertyValue(RestService item) {
			return item.getDescription();
		}		
	};

	@SuppressWarnings("serial")
	public static final Property<RestService> HELPGET = new AbstractProperty<RestService>("GET"){
		@Override
		public String getPropertyValue(RestService item) {
			try {
				return item.getHelpGet().getDescription();
			} 
			catch (NullPointerException e) {
				return null;
			}
		}		
	};

	@SuppressWarnings("serial")
	public static final Property<RestService> HELPPOST = new AbstractProperty<RestService>("POST"){
		@Override
		public String getPropertyValue(RestService item) {
			try{
				return item.getHelpPost().getDescription();
			} 
			catch (NullPointerException e) {
				return null;
			}				
		}		
	};
	
	@SuppressWarnings("serial")
	public static final Property<RestService> HELPDELETE = new AbstractProperty<RestService>("DELETE"){
		@Override
		public String getPropertyValue(RestService item) {
			try{
				return item.getHelpDelete().getDescription();
			} 
			catch (NullPointerException e) {
				return null;
			}				
		}		
	};
	
	private List<RestService> rcalls;
	
	public RestServiceProvider(List<RestService> rcalls) {
		this.rcalls=rcalls;
	}

	@Override
	protected List<org.geoserver.web.wicket.GeoServerDataProvider.Property<RestService>> getProperties() {
		return Arrays.asList(NAME,URL,DESCRIPTION,HELPGET,HELPPOST,HELPDELETE);	
	}

	@Override
	protected List<RestService> getItems() {
		return rcalls;
	}

}
