package cn.kanejin.webop.core;

import cn.kanejin.webop.core.exception.IllegalConfigException;

import java.util.HashMap;
import java.util.Map;

public class ConverterMapping {
	
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
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {

			throw new IllegalConfigException(
					"Can't instantiate converter class[" + className + "]." +
					" Check for the converter configuration", e);
		}
	}
}
