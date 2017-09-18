package cn.kanejin.webop.operation.action;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.kanejin.webop.Constants;
import cn.kanejin.webop.operation.OperationContext;

public class OperationReturnAction extends EndReturnAction {
	private static final Pattern PARAM_PATTERN = Pattern.compile("\\{[^\\}]+\\}");	
	
	public static OperationReturnAction getInstance(String opUri, String opParams) {
		return new OperationReturnAction(opUri, opParams);
	}

	private String opUri;
	private String opParams;
	
	private OperationReturnAction(String opUri, String opParams) {
		this.opUri = opUri;
		this.opParams = opParams;
	}

	@Override
	public void doAction(OperationContext oc) throws ServletException, IOException {
		HttpServletRequest req = oc.getRequest();
		HttpServletResponse res = oc.getResponse();

		saveOpParamsToSession(oc);

		String queryString = opParams == null ? "" : "?" + parseAttrs(req, opParams);
		
		res.sendRedirect(req.getContextPath() + parseAttrs(req, opUri) + queryString);
	}

	private static String parseAttrs(HttpServletRequest req, String sourceString) {
		
		if (sourceString == null || sourceString.isEmpty())
			return null;
		
		String resultString = sourceString;
		
		while (true) {
			Matcher matcher = PARAM_PATTERN.matcher(resultString);
			
			if (!matcher.find())
				break;
			
			String replaceHolder = matcher.group();
			String attrKey = replaceHolder.substring(1, replaceHolder.length() - 1);
			String attrValue = req.getAttribute(attrKey) == null ? "" : req.getAttribute(attrKey).toString();
			
			resultString = resultString.replace(replaceHolder, attrValue);
		};
		
		return resultString;
	}
	
	/**
	 * @return the opUri
	 */
	public String getOpUri() {
		return opUri;
	}

	/**
	 * @param opUri the opUri to set
	 */
	public void setOpUri(String opUri) {
		this.opUri = opUri;
	}
	
	@SuppressWarnings("unchecked")
	// Save attributes into HttpSession before jumping to another operation
	private void saveOpParamsToSession(OperationContext ctx) {
		Map<String, Object> attrs = new HashMap<String, Object>();
		Enumeration<String> attrNames = ctx.getRequest().getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String key = attrNames.nextElement();
			// 去掉request里operation上下文
			if ("operationContext".equals(key))
				continue ;
			attrs.put(key, ctx.getRequest().getAttribute(key));
		}
		
		ctx.getSession().setAttribute(Constants.OP_JUMP_PARAMS, attrs);
	}
	
	@Override
	public String toString() {
		return "operation " + opUri;
	}
}
