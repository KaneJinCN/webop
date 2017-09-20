package cn.kanejin.webop.core;

import java.io.Serializable;

public interface Converter<T> extends Serializable {
	String convert(T t);
}
