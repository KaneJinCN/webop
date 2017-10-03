package cn.kanejin.webop.sample.json;

import cn.kanejin.webop.core.Converter;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;


public class SimpleJsonConverter implements Converter<Map<String, Object>> {

	@Override
	public String convert(Map<String, Object> t) {
		ObjectMapper mapper = new ObjectMapper();
		
		for (String key : t.keySet()) {
			if (t.get(key) instanceof Date) {
				DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
				t.put(key, df.format(t.get(key)));
			}
		}

		try {
			return mapper.writeValueAsString(t);
		} catch (IOException e) {
		}

		return "{}";
	}
}
