package org.geoserver.egeosmanager.web.providers;

import java.util.Arrays;
import java.util.List;

import org.geoserver.egeosmanager.web.wrappers.RestParameter;
import org.geoserver.web.wicket.GeoServerDataProvider;

public class ParameterProvider  extends GeoServerDataProvider<RestParameter>{
	private static final long serialVersionUID = -1766567624533614937L;

	@SuppressWarnings("serial")
	public static final Property<RestParameter> NAME = new AbstractProperty<RestParameter>("name"){
		@Override
		public String getPropertyValue(RestParameter item) {
			return item.getName();
		}		
	};
	
	@SuppressWarnings("serial")
	public static final Property<RestParameter> DESCRIPTION = new AbstractProperty<RestParameter>("description"){
		@Override
		public String getPropertyValue(RestParameter item) {
			return item.getDescription();
		}		
	};
	
	private List<RestParameter> items;
	
	public ParameterProvider(List<RestParameter> items) {
		this.items=items;
	}

	@Override
	protected List<org.geoserver.web.wicket.GeoServerDataProvider.Property<RestParameter>> getProperties() {
		return Arrays.asList(NAME,DESCRIPTION);	
	}

	@Override
	protected List<RestParameter> getItems() {
		return items;
	}
}
