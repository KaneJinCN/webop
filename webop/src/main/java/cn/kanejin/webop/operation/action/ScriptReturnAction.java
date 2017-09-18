package cn.kanejin.webop.operation.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.MultipartFormdataRequest;
import cn.kanejin.webop.converter.Converter;
import cn.kanejin.webop.converter.ConverterFactory;
import cn.kanejin.webop.operation.OperationContext;

public class ScriptReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(ScriptReturnAction.class);
	
	public static ScriptReturnAction getInstance(String attribute, String converter) {
		return new ScriptReturnAction(attribute, converter);
	}
	
	private String attribute;
	
	private String converter;
	
	private ScriptReturnAction(String attribute, String converter) {
		this.attribute = attribute;
		this.converter = converter;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		Object jsonObj = req.getAttribute(attribute);

		if (jsonObj == null)
			throw new ServletException("json data to return is null");

		if (req instanceof MultipartFormdataRequest) 
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
	 * @param attribute the attribute to set
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}


	/**
	 * @return the converter
	 */
	public String getConverter() {
		return converter;
	}


	/**
	 * @param converter the converter to set
	 */
	public void setConverter(String converter) {
		this.converter = converter;
	}
	
	@Override
	public String toString() {
		return "script " + attribute + " " + converter;
	}
}
