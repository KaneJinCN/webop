package cn.kanejin.webop;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Kane Jin
 */
public abstract class ServletContextSupportFilter implements Filter {

	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.servletContext = filterConfig.getServletContext();
	}

	protected ServletContext getServletContext() {
		return servletContext;
	}
}
