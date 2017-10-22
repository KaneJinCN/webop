package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static cn.kanejin.commons.util.StringUtils.isNotEmpty;

public class JsonpReturnAction extends JsonConvertAction {
	private static final Logger log = LoggerFactory.getLogger(JsonpReturnAction.class);

	public static JsonpReturnAction build(String attribute, String callback, String converter) {
		return new JsonpReturnAction(attribute, callback, converter);
	}

	private final String callback;

	private JsonpReturnAction(String attribute, String callback, String converter) {
		super(attribute, converter);

		this.callback = isNotEmpty(callback) ? callback : "callback";
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		if (oc.isMultipart())
			res.setContentType("text/html");
		else
			res.setContentType("application/json");

		PrintWriter out = res.getWriter();
		
		String result = convertToJson(oc);

		String callbackName = req.getParameter(callback);
		if (callbackName != null && !callbackName.isEmpty()) {
			result = String.format("try{%s(%s)}catch(e){}", callbackName, result);
		}
		
		log.trace("JSONP RESULT: {}", result);

		out.print(result);
	}

	@Override
	public String toString() {
		return "json " + attribute + " " + converter;
	}
}
