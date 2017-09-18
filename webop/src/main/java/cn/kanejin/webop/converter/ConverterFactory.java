package cn.kanejin.webop.converter;

import cn.kanejin.webop.cache.WebopCacheManager;
import cn.kanejin.webop.operation.OperationException;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConverterFactory {
	
	private static final Logger log = LoggerFactory.getLogger(ConverterFactory.class);
	
	private static ConverterFactory factory;
	
	private ConverterFactory() {}

	public static ConverterFactory getInstance() {
		if (factory == null)
			factory = new ConverterFactory();
		return factory;
	}

	@SuppressWarnings("unchecked")
	public Converter<Object> create(String className) {
		try {

			Cache<String, Converter> cache = WebopCacheManager.getInstance().getConverterCache();

			Converter<Object> converter = cache.get(className);
			
			if (converter != null) return converter;
			
			converter = (Converter<Object>) Class.forName(className).newInstance();

			cache.put(className, converter);

			return converter;
		} catch (Exception e) {
			throw new OperationException("Instant XML Converter class[" + className + "] error", e);
		}
	}
}
