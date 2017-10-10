package cn.kanejin.webop.loader;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * @author Kane Jin
 */
public class ConfigEntityResolver implements EntityResolver {
	private static final Logger log = LoggerFactory.getLogger(ConfigEntityResolver.class);

	public static final String DEFAULT_SCHEMA_MAPPINGS_LOCATION = "META-INF/webop.schemas";

	private final String schemaMappingsLocation;

	private volatile Map<String, String> schemaMappings;

	private static ConfigEntityResolver resolver;

	private ConfigEntityResolver() {
		this.schemaMappingsLocation = DEFAULT_SCHEMA_MAPPINGS_LOCATION;
	}
	public static ConfigEntityResolver getInstance() {
		if (resolver == null)
			resolver = new ConfigEntityResolver();
		return resolver;
	}
	

	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException {
		log.debug("Trying to resolve XML entity with public id [{}] and system id [{}]",
				publicId, systemId);

		if (systemId != null) {
			String resLocation = getSchemaMappings().get(systemId);
			if (resLocation != null) {
				InputSource source = new InputSource(
						getClass().getClassLoader().getResourceAsStream(resLocation));
				source.setPublicId(publicId);
				source.setSystemId(systemId);
				log.debug("Found XML schema [{}] in : {}", systemId, resLocation);
				return source;
			}
		}
		return null;
	}

	private Map<String, String> getSchemaMappings() {
		if (schemaMappings != null)
			return schemaMappings;

		synchronized (this) {
			if (schemaMappings == null) {
				log.debug("Loading schema mappings from [{}]", schemaMappingsLocation);
				try {
					Properties mappings = new Properties();
					mappings.load(
							getClass().getClassLoader().getResourceAsStream(schemaMappingsLocation));

					log.debug("Loaded schema mappings: {}", mappings);

					Map<String, String> tempSchemaMappings = new ConcurrentHashMap<String, String>();

					for (Object obj : mappings.keySet()) {
						tempSchemaMappings.put((String)obj, (String)mappings.get(obj));
					}
					schemaMappings = tempSchemaMappings;
				} catch (IOException ex) {
					throw new IllegalStateException("Unable to load schema mappings from location ["
													+ schemaMappingsLocation + "]", ex);
				}
			}
		}

		return schemaMappings;
	}

	public String toString() {
		return "EntityResolver using mappings " + getSchemaMappings();
	}
}
