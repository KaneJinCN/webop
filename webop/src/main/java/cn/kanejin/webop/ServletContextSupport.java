package cn.kanejin.webop;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kane Jin
 */
public abstract class ServletContextSupport implements Filter {

	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.servletContext = filterConfig.getServletContext();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		doFilterInternal(
				(HttpServletRequest)request,
				(HttpServletResponse) response,
				chain);
	}

	protected abstract void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException;

	protected ServletContext getServletContext() {
		return servletContext;
	}
}
