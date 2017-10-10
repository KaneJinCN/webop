package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ScriptReturnAction extends JsonConvertAction {
	private static final Logger log = LoggerFactory.getLogger(ScriptReturnAction.class);
	
	public static ScriptReturnAction build(String attribute, String converter) {
		return new ScriptReturnAction(attribute, converter);
	}
	
	private ScriptReturnAction(String attribute, String converter) {
		super(attribute, converter);
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

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

		result += convertToJson(oc);

		result += ");";
		
		log.trace("SCRIPT RESULT: {}", result);

		out.print(result);
		
	}

	@Override
	public String toString() {
		return "script " + attribute + " " + converter;
	}
}
