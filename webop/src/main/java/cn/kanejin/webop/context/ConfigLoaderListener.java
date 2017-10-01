package cn.kanejin.webop.context;

import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.loader.ConfigXmlLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * @version $Id: OperationLoaderListener.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class ConfigLoaderListener implements ServletContextListener {

	private static final Logger log = LoggerFactory.getLogger(ConfigLoaderListener.class);

	private static final String CONFIG_LOCATION_KEY = "webopConfigLocation";

	@Override
	public void contextInitialized(ServletContextEvent event) {
		log.info("ContextInitialized");

		log.info("Initializing Webop Context");
		WebopContext.init();

		log.info("Loading webop configurations");
		loadConfigFromLocations(event.getServletContext());
	}

	/*
	 * 根据web.xml里配置的"webopConfigLocation"路径，寻找所有的webop配置并加载
	 */
	private void loadConfigFromLocations(ServletContext servletContext) {

		String configLocations = servletContext.getInitParameter(CONFIG_LOCATION_KEY);
		if (configLocations == null || configLocations.isEmpty())
			throw new IllegalArgumentException("context-param [" + CONFIG_LOCATION_KEY + "] not found in web.xml");

		String[] locations = configLocations.trim().split("(\\s*,\\s*)|(\\s+)");

		ConfigXmlLoader.getInstance().load(servletContext, locations);
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info("ContextDestroyed");

		WebopContext.get().getCacheManager().close();
	}
}
