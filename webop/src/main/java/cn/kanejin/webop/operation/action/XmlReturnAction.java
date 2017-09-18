package cn.kanejin.webop.operation.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.converter.Converter;
import cn.kanejin.webop.converter.ConverterFactory;
import cn.kanejin.webop.operation.OperationContext;

public class XmlReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(XmlReturnAction.class);

	public static XmlReturnAction getInstance(String attribute, String converter) {
		return new XmlReturnAction(attribute, converter);
	}

	private String attribute;
	
	private String converter;
	
	private XmlReturnAction(String attribute, String converter) {
		this.attribute = attribute;
		this.converter = converter;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		Object xmlObj = req.getAttribute(attribute);

		if (xmlObj == null)
			throw new ServletException("xml data to return is null");

		res.setContentType("text/xml");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();
		
		Converter<Object> conv = ConverterFactory.getInstance().create(converter);
		
		String xml = conv.convert(xmlObj);

		log.debug("XML RESULT: {}", xml);
		out.print(xml);
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
		return "xml " + attribute + " " + converter;
	}
}
