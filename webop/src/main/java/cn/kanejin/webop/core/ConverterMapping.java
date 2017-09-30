package cn.kanejin.webop.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ConverterMapping {
	
	private static final Logger log = LoggerFactory.getLogger(ConverterMapping.class);

	private final Map<String, Converter> converterMap;

	public ConverterMapping() {
		this.converterMap = new HashMap<>();
	}

	public Converter get(String className) {
		Converter converter = converterMap.get(className);

		if (converter == null) {
			converter = create(className);
			converterMap.put(className, converter);
		}

		return converter;
	}

	private Converter create(String className) {
		try {
			return (Converter) Class.forName(className).newInstance();
		} catch (Exception e) {
			throw new OperationException("Instant Converter class[" + className + "] error", e);
		}
	}
}
