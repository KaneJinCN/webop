package cn.kanejin.webop.interceptor;

import cn.kanejin.webop.core.Interceptor;
import cn.kanejin.webop.core.InterceptorChain;
import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

import static cn.kanejin.commons.util.StringUtils.isBlank;
import static cn.kanejin.commons.util.StringUtils.isNotEmpty;


public class AuthInterceptor implements Interceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
	
	private String authSessionKey;
	private String authLoginUrl;

	@Override
	public void init(Map<String, String> params) {
		
		authSessionKey = params.get("authSessionKey");
		
		if (isBlank(authSessionKey))
			throw new IllegalArgumentException("Parameter authSessionKey is required");

		authLoginUrl = params.get("authLoginUrl");

		if (isBlank(authLoginUrl))
			authLoginUrl = "/login";
	}

	@Override
	public void intercept(OperationContext context, InterceptorChain chain) throws ServletException, IOException {
		
		if (!hasLoggedIn(context)) {
			
			// Ajax
			if ("XMLHttpRequest".equals(context.getRequest().getHeader("x-requested-with"))) {
				context.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return ;
			}
			
			// Html
			context.setBackUrl(getCurrentUrl(context.getRequest()));
			
			String redirectUrl = context.getRequest().getContextPath() + authLoginUrl;
			
			log.debug("Redirecting to login page \"{}\"", redirectUrl);

			context.getResponse().sendRedirect(redirectUrl);
			
			return ;
		}
		
		chain.intercept(context);
	}

	private boolean hasLoggedIn(OperationContext context) {
		HttpSession session = context.getRequest().getSession(false);

		if (session == null)
			return false;

		return session.getAttribute(authSessionKey) != null;
	}

	private String getCurrentUrl(HttpServletRequest request) {
		if (request == null)
			return "";
		
		String url = request.getRequestURI();

		String queryString = request.getQueryString();
		if (isNotEmpty(queryString))
			url += ("?" + queryString);
	
		return url;
	}

	@Override
	public void destroy() {}
}
