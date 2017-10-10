package cn.kanejin.webop.core.action;

import cn.kanejin.webop.MultipartRequestWrapper;
import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static cn.kanejin.commons.util.StringUtils.nullToEmpty;

public class TextReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(TextReturnAction.class);

	public static TextReturnAction build(String value) {
		return new TextReturnAction(value);
	}

	private final String value;
	
	private TextReturnAction(String value) {
		this.value = nullToEmpty(value);
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		if (req instanceof MultipartRequestWrapper)
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
}
