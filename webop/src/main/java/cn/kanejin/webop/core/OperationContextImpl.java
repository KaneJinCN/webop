package cn.kanejin.webop.core;

import cn.kanejin.webop.MultipartRequestWrapper;
import cn.kanejin.webop.core.Message.Level;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

import static cn.kanejin.webop.core.Constants.*;

/**
 * @version $Id: OperationContextImpl.java 167 2017-09-13 09:26:47Z Kane $
 * @author Kane Jin
 */
public class OperationContextImpl implements OperationContext {
	private static final Logger log = LoggerFactory.getLogger(OperationContextImpl.class);
	
	private HttpServletRequest req;
	private HttpServletResponse res;
	private ServletContext sc;

	@SuppressWarnings("unchecked")
	public OperationContextImpl(HttpServletRequest req, HttpServletResponse res, ServletContext sc) {
		this.req = req;
		this.res = res;
		this.sc = sc;
		req.setAttribute("operationContext", this);
		
		// Add attributes from previous operation into request
		HttpSession session = req.getSession(false);
		if (session != null) {
			Map<String, Object> attrs = (Map<String, Object>) session.getAttribute(OP_JUMP_PARAMS);
			session.removeAttribute(OP_JUMP_PARAMS);
			if (attrs != null)
				for (String key : attrs.keySet()) {
					req.setAttribute(key, attrs.get(key));
				}			
		}

		// Add parameters into attributes in request
		Enumeration<String> paramEnum = req.getParameterNames();
		while (paramEnum.hasMoreElements()) {
			String name = paramEnum.nextElement();
			// Array parameter if its name ends with "[]"
			if (name.endsWith("[]")) {
				req.setAttribute(name.substring(0, name.length() - 2), req.getParameterValues(name));
			} else {
				req.setAttribute(name, req.getParameter(name));
			}
		}
		
		// Add path variables into attributes in request
		Map<String, String> pathVars =
			(Map<String, String>) req.getAttribute(PATH_VAR);
		
		if (pathVars != null)
			for (String key : pathVars.keySet())
				req.setAttribute(key, pathVars.get(key));

	}

	@Override
	public HttpServletRequest getRequest() {
		return req;
	}
	
	@Override
	public HttpServletResponse getResponse() {
		return res;
	}
	
	@Override
	public HttpSession getSession() {
		return req.getSession();
	}

	@Override
	public ServletContext getServletContext() {
		return sc;
	}

	@Override
	public String getParameter(String paramKey) {
		return getAttribute(paramKey, String.class);
	}
	
	@Override
	public String getParameter(String paramKey, String defaultValue) {
		String result = getAttribute(paramKey, String.class);
		
		return result != null ? result : defaultValue;
	}
	
	@Override
	public boolean isMultipart() {
		return req instanceof MultipartRequestWrapper;
	}

	@Override
	public Enumeration<String> getFileItemKeys() {
		if (!isMultipart())
			return null;

		MultipartRequestWrapper mreq = (MultipartRequestWrapper) req;
		return mreq.getFileItemKeys();
	}

	@Override
	public FileItem getFileItem(String fileItemKey) {
		if (!isMultipart())
			return null;

		MultipartRequestWrapper mreq = (MultipartRequestWrapper) req;
		return mreq.getFileItem(fileItemKey);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getAttribute(String attKey, Class<T> clazz) {
		return (T) req.getAttribute(attKey);
	}

	@Override
	public void setAttribute(String attKey, Object obj) {
		req.setAttribute(attKey, obj);
	}

	@Override
	public void removeAttribute(String attKey) {
		req.removeAttribute(attKey);
	}

	@Override
	public void setBackUrl(String url) {
		if (url == null || url.isEmpty())
			getSession().removeAttribute(BACK_URL);
		else
			if (url.startsWith("/")) 
				getSession().setAttribute(BACK_URL, url);
	}

	@Override
	public String getBackUrl() {
		return (String)getSession().getAttribute(BACK_URL);
	}
	
	@Override
	public String newToken() {
		String token = Long.toHexString(Math.abs(UUID.randomUUID().getMostSignificantBits()));
		log.debug("Generate new token : {}", token);
		
		getSession().setAttribute(TOKEN, token);

		return token;
	}

	@Override
	public String getToken() {
		String token = (String) getSession().getAttribute(TOKEN);
		
		if (token == null || token.isEmpty())
			token = newToken();
			
		return token;
	}

	@Override
	public void cleanToken() {
		if (log.isDebugEnabled())
			log.debug("Remove token [{}] from session [{}]",
					  getSession().getAttribute(TOKEN), getSession().getId());
		getSession().removeAttribute(TOKEN);
	}

	@Override
	public boolean checkToken(String requestToken) {
		if (requestToken == null || requestToken.isEmpty())
			return false;

		String sessionToken = (String) getSession().getAttribute(TOKEN);
		
		log.debug("Compare token : (session){} <-> (page){}", sessionToken, requestToken);

		if (sessionToken == null || sessionToken.isEmpty())
			return false;
		
		if (!requestToken.equalsIgnoreCase(sessionToken))
			return false;
		
		cleanToken();
		
		return true;
	}

	@Override
	public Object getAttribute(String attKey) {
		return req.getAttribute(attKey);
	}

	@Override
	public String getIp() {
		
		String ip = getRequest().getHeader("x-forwarded-for");
		if (ip == null || ip.isEmpty()) {
			ip = getRequest().getRemoteAddr();
		} else if (ip.indexOf(",") >= 0) {
			ip = ip.substring(0, ip.indexOf(","));
		}

		return ip;
	}

	@Override
	public void setMessage(Level level, String text) {
		setAttribute(MESSAGE, new Message(level, text));
	}

	@Override
	public Message getMessage() {
		return (Message) getAttribute(MESSAGE);
	}

	@Override
	public String getCurrentUrl(boolean withQueryString) {
		if (!withQueryString)
			return getRequest().getRequestURI();
		
		
		String url = getRequest().getRequestURI();

		String queryString = getRequest().getQueryString();
		if (queryString != null && !queryString.isEmpty())
			url += ("?" + queryString);
	
		return url;
	}
}
