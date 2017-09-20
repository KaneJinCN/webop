package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.Converter;
import cn.kanejin.webop.core.ConverterFactory;
import cn.kanejin.webop.core.OperationContext;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static cn.kanejin.commons.util.StringUtils.isNotEmpty;

public class JsonpReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(JsonpReturnAction.class);

	public static JsonpReturnAction getInstance(String attribute, String callback, String converter) {
		return new JsonpReturnAction(attribute, callback, converter);
	}

	private final String attribute;
	private final String callback;
	
	private final String converter;
	
	private JsonpReturnAction(String attribute, String callback, String converter) {
		this.attribute = attribute;

		this.callback = isNotEmpty(callback) ? callback : "callback";

		this.converter = converter;
	}

	@Override
	public void handleAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		Object jsonObj = req.getAttribute(attribute);

		if (jsonObj == null)
			throw new ServletException("json data to return is null");

		if (oc.isMultipart())
			res.setContentType("text/html");
		else
			res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();
		
		String result = "";
		
		if (isNotEmpty(converter)) {
			Converter<Object> conv = ConverterFactory.getInstance().create(converter);
			result = conv.convert(jsonObj);
		} else {
			ObjectMapper mapper = new ObjectMapper();
			result = mapper.writeValueAsString(jsonObj);
		}
		
		String callbackName = req.getParameter(callback);
		if (callbackName != null && !callbackName.isEmpty()) {
			result = String.format("try{%s(%s)}catch(e){}", callbackName, result);
		}
		
		log.debug("JSONP RESULT: {}", result);

		out.print(result);
	}

	/**
	 * @return the attribute
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * @return the converter
	 */
	public String getConverter() {
		return converter;
	}

	@Override
	public String toString() {
		return "json " + attribute + " " + converter;
	}
}
