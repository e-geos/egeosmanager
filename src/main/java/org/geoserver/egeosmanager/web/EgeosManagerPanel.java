package org.geoserver.egeosmanager.web;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.geoserver.config.GeoServer;
import org.geoserver.egeosmanager.annotations.Help;
import org.geoserver.egeosmanager.annotations.Parameter;
import org.geoserver.egeosmanager.rest.EgeosRestMapping;
import org.geoserver.egeosmanager.web.providers.DependencyProvider;
import org.geoserver.egeosmanager.web.providers.RestServiceProvider;
import org.geoserver.egeosmanager.web.wrappers.Dependency;
import org.geoserver.egeosmanager.web.wrappers.RestCall;
import org.geoserver.egeosmanager.web.wrappers.RestParameter;
import org.geoserver.egeosmanager.web.wrappers.RestService;
import org.geoserver.rest.format.DataFormat;
import org.geoserver.web.CatalogIconFactory;
import org.geoserver.web.GeoServerSecuredPage;
import org.geoserver.web.wicket.GeoServerDataProvider.Property;
import org.geoserver.web.wicket.GeoServerTablePanel;
import org.geoserver.web.wicket.Icon;
import org.geotools.util.logging.Logging;

public class EgeosManagerPanel extends GeoServerSecuredPage {
	private static final long serialVersionUID = 2153628904293526395L;
		
	protected static final Logger LOGGER = Logging.getLogger(EgeosManagerPanel.class);
	 	
	@SuppressWarnings("serial")
	private List<String> dependencies = new ArrayList<String>(){{
		add("org.geoserver.printing");
		add("org.geoserver.rest");
		add("org.geoserver.web");
		add("org.geoserver.gwc");
		add("org.geoserver.gdal");
		add("org.geoserver.map.turbojpeg");
		add("org.geotools.data");
		add("oracle.jdbc");
	}};
	
	@SuppressWarnings("serial")
	public EgeosManagerPanel() {
		super();
		
		final ModalWindow window=new ModalWindow("dialog");		
		add(window);

		List<Dependency> pkgs=new ArrayList<Dependency>(){{
			Package pkg = this.getClass().getPackage();
			add(new Dependency(pkg));
			for(String d:dependencies)
				try {
					add(new Dependency(Package.getPackage(d)));					
				} 
				catch (Exception e) {
					add(new Dependency(d));
					LOGGER.log(Level.SEVERE,"Missing required lib "+d);
				}		
		}};		
		
		add(new GeoServerTablePanel<Dependency>("deps", new DependencyProvider(pkgs)){{
				setFilterable(false);
			}
		
			@Override
			protected Component getComponentForProperty(String id, IModel<Dependency> itemModel,Property<Dependency> property) {
				if(property == DependencyProvider.FOUND){
					Boolean enabled = (Boolean) property.getPropertyValue(itemModel.getObject());
                    return new Icon(id, enabled?CatalogIconFactory.ENABLED_ICON : CatalogIconFactory.DISABLED_ICON);
				} 
				else
					return new Label(id,(String) property.getPropertyValue(itemModel.getObject()));
			}
		});
			
		List<RestService> rcalls=new ArrayList<RestService>(){{
			GeoServer gs=getGeoServer();
			String bu = gs.getGlobal().getSettings().getProxyBaseUrl();			
			bu=bu!=null?bu+"rest":"/geoserver/rest";
			List<EgeosRestMapping> mappings = getGeoServerApplication().getBeansOfType(EgeosRestMapping.class);

			//It should be a list with only one element			
			for(EgeosRestMapping rm:mappings){				
				
				@SuppressWarnings("rawtypes")
				Map m = rm.getRoutes();
				
				for(Object r:m.keySet())									
					add(parseHelps(m.get(r).toString(),r.toString(),bu));				
			}
		}};
		
		add(new GeoServerTablePanel<RestService>("restCalls",new RestServiceProvider(rcalls)){{
				setFilterable(false);
			}
		
			@Override
			protected Component getComponentForProperty(String id,final IModel<RestService> itemModel,Property<RestService> property) { 
				final String val = (String) property.getPropertyValue(itemModel.getObject());				
								
				if (val!=null && !val.trim().isEmpty() && (property == RestServiceProvider.HELPGET || property == RestServiceProvider.HELPPOST ||property == RestServiceProvider.HELPDELETE)){
					final String name = property.getName();
					final RestService service = itemModel.getObject();
					final String url=service.getAbsoluteUrl();
					return new AjaxLink<Void>(id) {{
							setBody(new Model<String>(name.toUpperCase()));
						}
					
						@Override
						public void onClick(AjaxRequestTarget target) {	 
							window.setContent(new RestCallDetails(
								window.getContentId(),
								name.toUpperCase()+" "+url,
								val,
								service.getRequired(name),
								service.getOptinal(name)
							));
							window.show(target);
						}						
					};										
				}
				else
					return new Label(id,val);
			}			
		});
	}
	
	private RestService parseHelps(String beanName,String url,String base){
		Object rr = getGeoServerApplication().getBean(beanName);
		String help=null;
		RestCall helpGet=null;
		RestCall helpPost=null;
		RestCall helpDelete=null;

		if (rr!=null){					
			Class<? extends Object> cls = rr.getClass();	
			if (cls!=null){
				try {									
					help=cls.getAnnotation(Help.class).text();						
				} 
				catch (NullPointerException e) {				
				}
				
				helpGet=getMethodAnn(cls,"handleGetBody",DataFormat.class);
				helpPost=getMethodAnn(cls,"handlePostBody",HashMap.class, DataFormat.class);
				helpDelete=getMethodAnn(cls,"handleDeleteBody",HashMap.class, DataFormat.class);
			}
		}
		
		return new RestService(beanName, url, base, help, helpGet, helpPost, helpDelete);
	}
	
	@SuppressWarnings("serial")
	private RestCall getMethodAnn(Class<?> cls,String mthd,Class<?>... parameters){
		RestCall res=null;
		try {
			Method mth = cls.getDeclaredMethod(mthd,parameters);
			Help ann = mth.getAnnotation(Help.class);
			if(ann!=null){
				res=new RestCall();
				res.setDescription(ann.text());
				res.setRequired(new ArrayList<RestParameter>(){{
					for(Parameter p:ann.requires())
						add(new RestParameter(p.name(), p.description()));
				}});
				res.setOptional(new ArrayList<RestParameter>(){{
					for(Parameter p:ann.optionals())
						add(new RestParameter(p.name(), p.description()));
				}});	
			}
		} 
		catch (NoSuchMethodException | SecurityException |NullPointerException e) {		
			LOGGER.info("Error "+cls+"."+mthd+" "+e.getClass()+": "+e.getMessage()+".");
		}
		return res;
	}
}
	