package cn.kanejin.webop.converter;

import java.io.Serializable;

public interface Converter<T> extends Serializable {
	String convert(T t);
}
