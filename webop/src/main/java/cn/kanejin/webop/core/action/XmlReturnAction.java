package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.Converter;
import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.WebopContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class XmlReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(XmlReturnAction.class);

	public static XmlReturnAction build(String attribute, String converter) {
		return new XmlReturnAction(attribute, converter);
	}

	private final String attribute;
	
	private final String converter;
	
	private XmlReturnAction(String attribute, String converter) {
		this.attribute = attribute;
		this.converter = converter;
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		Object xmlObj = req.getAttribute(attribute);

		if (xmlObj == null)
			throw new ServletException("xml data to return is null");

		res.setContentType("text/xml");

		PrintWriter out = res.getWriter();
		
		Converter<Object> c = WebopContext.get().getConverterMapping().get(converter);
		
		String xml = c.convert(xmlObj);

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
	 * @return the converter
	 */
	public String getConverter() {
		return converter;
	}

	@Override
	public String toString() {
		return "xml " + attribute + " " + converter;
	}
}
