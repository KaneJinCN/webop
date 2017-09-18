package cn.kanejin.webop.operation.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.MultipartFormdataRequest;
import cn.kanejin.webop.operation.OperationContext;

public class AttributeReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(AttributeReturnAction.class);

	public static AttributeReturnAction getInstance(String attribute) {
		return new AttributeReturnAction(attribute);
	}

	private String attribute;
	
	
	private AttributeReturnAction(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		if (req instanceof MultipartFormdataRequest) 
			res.setContentType("text/html");
		else
			res.setContentType("text/plain");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();

		Object textObj = req.getAttribute(attribute);
		out.print(textObj == null ? "" : textObj.toString());
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

	@Override
	public String toString() {
		return "attribute " + attribute;
	}
}
