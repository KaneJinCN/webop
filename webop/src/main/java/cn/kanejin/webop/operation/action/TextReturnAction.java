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

public class TextReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(TextReturnAction.class);

	public static TextReturnAction getInstance(String value) {
		return new TextReturnAction(value);
	}

	private String value;
	
	
	private TextReturnAction(String value) {
		this.value = value == null ? "" : value;
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

		out.print(value);
	}

	@Override
	public String toString() {
		return "text " + value;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
