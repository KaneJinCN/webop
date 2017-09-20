package cn.kanejin.webop.sample.xml;

import java.util.Date;
import java.util.Map;

import cn.kanejin.webop.core.Converter;

public class SimpleXmlConverter implements Converter<Map<String, Object>> {

	@Override
	public String convert(Map<String, Object> t) {
		if (t == null || t.isEmpty()) {
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("<result>");
		for (String key : t.keySet()) {
			sb.append("<" + key + ">");
			if (t.get(key) instanceof Date)
				sb.append(((Date)t.get(key)).getTime());
			else
				sb.append(t.get(key));
			sb.append("</" + key + ">");
		}
		sb.append("</result>");
		
		return sb.toString();
	}
}
