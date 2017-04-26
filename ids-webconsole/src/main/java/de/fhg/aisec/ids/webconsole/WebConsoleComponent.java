package de.fhg.aisec.ids.webconsole;


import java.util.Optional;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.prefs.PreferencesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fhg.aisec.ids.api.cm.ContainerManager;
import de.fhg.aisec.ids.api.router.RouteManager;

/**
 * IDS management console, reachable at http://localhost:8181/ids/ids.html.
 * 
 * This OSGi component registers a web application, as soon as it is bound to an
 * <code>HttpService</code> interface. The web application will allow to control
 * and configure the IDS Core Platform and the connected services.
 * 
 * Note that the web console does not enforce any authorization or encryption
 * and provides everyone with the ability to control highly critical settings of
 * the core platform. It must only be used for demonstration purposes and must
 * be deactivated and/or removed in productive use.
 * 
 * @author Julian Schuette (julian.schuette@aisec.fraunhofer.de)
 *
 */
@Component(name="ids-webconsole")
public class WebConsoleComponent {
	private static final Logger LOG = LoggerFactory.getLogger(WebConsoleComponent.class);
	private static Optional<PreferencesService> configService = Optional.empty();
	private static Optional<ContainerManager> cml = Optional.empty();
	private static Optional<RouteManager> rm = Optional.empty();
	
	@Activate
	protected void activate(ComponentContext componentContext) {
		LOG.info("IDS webconsole activated");
	}
	
	@Deactivate
	protected void deactivate(ComponentContext componentContext) throws Exception {
		LOG.info("IDS webconsole deactivated");
	}
	
	@Reference(name = "cml.service",
            service = ContainerManager.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unbindContainerManagerService")
	protected void bindContainerManagerService(ContainerManager cml) {
		LOG.info("Bound to container manager");
		WebConsoleComponent.cml= Optional.of(cml);
	}

	protected void unbindContainerManagerService(ContainerManager http) {
		WebConsoleComponent.cml = Optional.empty();		
	}
	
	public static Optional<ContainerManager> getContainerManager() {
		return WebConsoleComponent.cml;
	}

	@Reference(name = "config.service",
            service = PreferencesService.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unbindConfigurationService")
	public void bindConfigurationService(PreferencesService conf) {
		LOG.info("Bound to configuration service");
		WebConsoleComponent.configService = Optional.of(conf);
	}

	public void unbindConfigurationService(PreferencesService conf) {
		WebConsoleComponent.configService = Optional.empty();
	}

	public static Optional<PreferencesService> getConfigService() {
		return WebConsoleComponent.configService;
	}
	
	@Reference(name = "rm.service",
            service = RouteManager.class,
            cardinality = ReferenceCardinality.OPTIONAL,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unbindRouteManagerService")
	protected void bindRouteManagerService(RouteManager rm) {
		LOG.info("Bound to route manager");
		WebConsoleComponent.rm  = Optional.of(rm);
	}

	protected void unbindRouteManagerService(RouteManager rm) {
		WebConsoleComponent.rm = Optional.empty();		
	}
	
	public static Optional<RouteManager> getRouteManager() {
		return WebConsoleComponent.rm;
	}
}