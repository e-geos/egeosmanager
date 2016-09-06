package org.geoserver.egeosmanager.web;

import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.geoserver.egeosmanager.web.providers.ParameterProvider;
import org.geoserver.egeosmanager.web.wrappers.RestParameter;
import org.geoserver.web.wicket.GeoServerDataProvider.Property;
import org.geoserver.web.wicket.GeoServerTablePanel;

public class RestCallDetails extends Panel{
	private static final long serialVersionUID = 5840893483211484075L;
	
	@SuppressWarnings("serial")
	public RestCallDetails(String id,String title,String description,List<RestParameter> required,List<RestParameter> optional) {
		super(id);
		
		add(new Label("title",title));
		add(new Label("description",description).setEscapeModelStrings(false));
		
		add(new GeoServerTablePanel<RestParameter>("required_table",new ParameterProvider(required)){{
				setFilterable(false);
				setPageable(false);
			}

			@Override
			protected Component getComponentForProperty(String id, IModel<RestParameter> itemModel,Property<RestParameter> property) {
				return new Label(id,(String) property.getPropertyValue(itemModel.getObject()));				
			}

		});
		
		add(new GeoServerTablePanel<RestParameter>("optional_table",new ParameterProvider(optional)){{
				setFilterable(false);
				setPageable(false);
			}

			@Override
			protected Component getComponentForProperty(String id, IModel<RestParameter> itemModel,Property<RestParameter> property) {
				return new Label(id,(String) property.getPropertyValue(itemModel.getObject()));
			}

		});

	}	
}
