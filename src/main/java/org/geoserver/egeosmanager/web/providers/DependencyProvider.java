package org.geoserver.egeosmanager.web.providers;

import java.util.Arrays;
import java.util.List;

import org.geoserver.egeosmanager.web.wrappers.Dependency;
import org.geoserver.web.wicket.GeoServerDataProvider;

public class DependencyProvider extends GeoServerDataProvider<Dependency>{
	private static final long serialVersionUID = -5402363625683445849L;
    
	@SuppressWarnings("serial")
	public static final Property<Dependency> NAME = new AbstractProperty<Dependency>("name"){
		@Override
		public String getPropertyValue(Dependency item) {
			return item.getName();
		}		
	};
	
	@SuppressWarnings("serial")
	public static final Property<Dependency> VERSION = new AbstractProperty<Dependency>("version"){
		@Override
		public String getPropertyValue(Dependency item) {
			return item.getVersion();
		}		
	};

	@SuppressWarnings("serial")
	public static final Property<Dependency> AUTHOR = new AbstractProperty<Dependency>("author"){
		@Override
		public String getPropertyValue(Dependency item) {
			return item.getAuthor();
		}		
	};
	
	@SuppressWarnings("serial")
	public static final Property<Dependency> FOUND = new AbstractProperty<Dependency>("found") {
        public Boolean getPropertyValue(Dependency item) {
            return Boolean.valueOf(item.getFound());
        }
    };

	private List<Dependency> items;
	
	public DependencyProvider(List<Dependency> items) {
		super();
		this.items = items;
	}

	@Override
	protected List<org.geoserver.web.wicket.GeoServerDataProvider.Property<Dependency>> getProperties() {
		return Arrays.asList(NAME,AUTHOR,VERSION,FOUND);
	}

	@Override
	protected List<Dependency> getItems() {
		return items;
	}

}
