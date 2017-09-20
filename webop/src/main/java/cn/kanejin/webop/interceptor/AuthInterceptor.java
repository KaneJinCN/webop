package cn.kanejin.webop.interceptor;

import cn.kanejin.webop.core.Interceptor;
import cn.kanejin.webop.core.InterceptorChain;
import cn.kanejin.webop.core.OperationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class AuthInterceptor implements Interceptor {
	private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);
	
	private String authSessionKey;
	private String authRedirectUrl;

	@Override
	public void init(Map<String, String> params) {
		
		authSessionKey = params.get("authSessionKey");
		
		if (authSessionKey == null || authSessionKey.isEmpty())
			throw new IllegalArgumentException("Parameter authSessionKey is required");
			
		authRedirectUrl = params.get("authRedirectUrl");
		
		if (authRedirectUrl == null || authRedirectUrl.isEmpty())
			throw new IllegalArgumentException("Parameter authRedirectUrl is required");
	}

	@Override
	public void intercept(OperationContext context, InterceptorChain chain) throws ServletException, IOException {
		
		boolean isLoggedIn = context.getSession().getAttribute(authSessionKey) != null;
		
		if (!isLoggedIn) {
			
			// Ajax
			if ("XMLHttpRequest".equals(context.getRequest().getHeader("x-requested-with"))) {
				context.getResponse().setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				return ;
			}
			
			// Html
			context.setBackUrl(getCurrentUrl(context.getRequest()));
			
			String redirectUrl = context.getRequest().getContextPath() + authRedirectUrl;
			
			log.debug("redirecting to \"{}\"", redirectUrl);

			context.getResponse().sendRedirect(redirectUrl);
			
			return ;
		}
		
		chain.intercept(context);
	}
	
	private static String getCurrentUrl(HttpServletRequest request) {
		if (request == null)
			return "";
		
		String url = request.getRequestURI();

		String queryString = request.getQueryString();
		if (queryString != null && !queryString.isEmpty())
			url += ("?" + queryString);
	
		return url;
	}

	@Override
	public void destroy() {}
}
