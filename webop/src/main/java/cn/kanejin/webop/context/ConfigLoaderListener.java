package cn.kanejin.webop.context;

import cn.kanejin.webop.cache.EhCacheManagerImpl;
import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.loader.ConfigXmlLoader;
import cn.kanejin.webop.support.PathPatternResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.IOException;

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
		WebopContext.get().setCacheManager(new EhCacheManagerImpl());

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

		String[] locations = configLocations.split("(\\s*,\\s*)|(\\s+)");

		for (String location : locations) {
			ConfigXmlLoader.getInstance().load(
					parseConfigLocations(servletContext, location.trim()));
		}
	}


	/*
	 * 解析通配符路径，把所有匹配的文件全部找出
	 */
	private String[] parseConfigLocations(ServletContext sc, String configLocation) {
		String locationPath = configLocation.substring(0, configLocation.lastIndexOf("/"));
		while (locationPath.contains("*") || locationPath.contains("?")) {
			locationPath = locationPath.substring(0, locationPath.lastIndexOf("/"));
		}

		String pattern = configLocation.substring(locationPath.length() + 1);
		String basePath = sc.getRealPath(locationPath);

		try {
			return PathPatternResolver.resolve(basePath, pattern);
		} catch (IOException e) {
			log.warn("Fail to parse location [" + basePath + "] : " + e.getMessage());
			return null;
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info("ContextDestroyed");

		WebopContext.get().getCacheManager().close();
	}
}
