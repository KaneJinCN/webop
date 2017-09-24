package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class AttributeReturnAction extends EndReturnAction {
	private static final Logger log = LoggerFactory.getLogger(AttributeReturnAction.class);

	public static AttributeReturnAction getInstance(String attribute) {
		return new AttributeReturnAction(attribute);
	}

	private final String attribute;

	private AttributeReturnAction(String attribute) {
		this.attribute = attribute;
	}

	@Override
	public void doActionInternal(OperationContext context)
			throws ServletException, IOException {

		HttpServletRequest req = context.getRequest();
		HttpServletResponse res = context.getResponse();

		if (context.isMultipart())
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

	@Override
	public String toString() {
		return "attribute " + attribute;
	}
}
