package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JsonReturnAction extends JsonConvertAction {
	private static final Logger log = LoggerFactory.getLogger(JsonReturnAction.class);

	public static JsonReturnAction build(String attribute, String converter) {
		return new JsonReturnAction(attribute, converter);
	}

	private JsonReturnAction(String attribute, String converter) {
		super(attribute, converter);
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletResponse res = oc.getResponse();

		if (oc.isMultipart())
			res.setContentType("text/html");
		else
			res.setContentType("application/json");
		res.setCharacterEncoding("UTF-8");

		PrintWriter out = res.getWriter();
		
		String result = convertToJson(oc);
		
		log.trace("JSON RESULT: {}", result);

		out.print(result);
	}

	@Override
	public String toString() {
		return "json " + attribute + " " + converter;
	}
}
