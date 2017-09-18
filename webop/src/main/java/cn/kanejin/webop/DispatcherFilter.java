package cn.kanejin.webop;

import cn.kanejin.webop.operation.Operation;
import cn.kanejin.webop.operation.OperationContext;
import cn.kanejin.webop.operation.OperationContextImpl;
import cn.kanejin.webop.operation.OperationMapping;
import cn.kanejin.webop.support.AntPathMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.kanejin.commons.util.StringUtils.*;

/**
 * @version $Id: MultipartFormdataFilter.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class DispatcherFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(DispatcherFilter.class);

	private List<String> ignorePatterns;

	private ServletContext servletContext;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		this.servletContext = filterConfig.getServletContext();

		initIgnorePatterns(filterConfig.getInitParameter("ignorePatterns"));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
														throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (ignoreURI(req)) {
			log.trace("Request[{}] is ignored", req.getRequestURI());
			chain.doFilter(request, response);

			return ;
		}

		Operation op = OperationMapping.getInstance().getOperation(req);

		if(op == null) {
			log.warn("No operation found for HTTP request with URI [{}]", req.getRequestURI());
			chain.doFilter(request, response);
		} else {
			log.info("URI = [{}], Operation URI = [{}]", req.getRequestURI(), op.getUri());
			log.info("User-Agent = [{}]", req.getHeader("User-Agent"));

			OperationContext ctx = new OperationContextImpl(req, res, servletContext);

			op.operate(ctx);
		}
	}

	@Override
	public void destroy() {}


	private void initIgnorePatterns(String ignorePatternsConfig) {
		if (isNotBlank(ignorePatternsConfig)) {
			ignorePatterns = new ArrayList<String>();

			String[] patterns = ignorePatternsConfig.split("(\\s*,\\s*)|(\\s+)");
			for (String p : patterns) {
				ignorePatterns.add(p.trim());
			}
		}
	}

	private boolean ignoreURI(HttpServletRequest req) {

		if (ignorePatterns == null)
			return false;

		String uri = parseURIPath(req);

		for (String pattern : ignorePatterns) {
			if (AntPathMatcher.matches(pattern, uri))
				return true;
		}

		return false;
	}

	private String parseURIPath(HttpServletRequest req) {

		String uri = req.getRequestURI();
		if (isEmpty(uri)) {
			return "/";
		}

		String contextPath = req.getContextPath();
		if (isNotEmpty(contextPath) && uri.startsWith(contextPath)) {
			uri = uri.substring(contextPath.length());
		}

		uri = uri.replaceAll("//", "/");

		if (!uri.startsWith("/")) {
			uri = "/" + uri;
		}

		if (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);

		return uri;
	}

}
