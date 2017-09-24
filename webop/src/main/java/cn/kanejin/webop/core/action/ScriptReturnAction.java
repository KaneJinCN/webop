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

public class ScriptReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(ScriptReturnAction.class);
	
	public static ScriptReturnAction getInstance(String attribute, String converter) {
		return new ScriptReturnAction(attribute, converter);
	}
	
	private final String attribute;
	
	private final String converter;
	
	private ScriptReturnAction(String attribute, String converter) {
		this.attribute = attribute;
		this.converter = converter;
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		Object jsonObj = req.getAttribute(attribute);

		if (jsonObj == null)
			throw new ServletException("json data to return is null");

		if (oc.isMultipart())
			res.setContentType("text/html");
		else
			res.setContentType("application/x-javascript");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();
		
		String callback = req.getParameter("callback");
		if (callback == null || callback.isEmpty())
			throw new ServletException("request parameter 'callback' is not specified!");
		
		String result = callback + "(";
		
		if (converter != null && !converter.isEmpty()) {
			Converter<Object> conv = ConverterFactory.getInstance().create(converter);
			result += conv.convert(jsonObj);
		} else {
			ObjectMapper mapper = new ObjectMapper();
			result += mapper.writeValueAsString(jsonObj);
		}
		
		result += ");";
		
		log.debug("SCRIPT RESULT: {}", result);

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
		return "script " + attribute + " " + converter;
	}
}
