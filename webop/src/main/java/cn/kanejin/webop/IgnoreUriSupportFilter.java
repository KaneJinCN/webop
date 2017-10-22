package cn.kanejin.webop;

import cn.kanejin.webop.support.AntPathMatcher;
import cn.kanejin.webop.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public abstract class IgnoreUriSupportFilter extends ServletContextSupportFilter {
	private static final Logger log = LoggerFactory.getLogger(IgnoreUriSupportFilter.class);

	private List<AntPathMatcher> ignorePatternMatchers;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		super.init(filterConfig);

		initIgnorePatterns(filterConfig.getInitParameter("ignoreUriPatterns"));
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if (ignoreURI(req)) {
			log.trace("Ignore request[{}]", req.getRequestURI());
			chain.doFilter(req, res);
		} else {
			doFilterInternal(req, res, chain);
		}
	}

	protected abstract void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException;

	private void initIgnorePatterns(String ignorePatternsConfig) {
		if (isNotBlank(ignorePatternsConfig)) {
			ignorePatternMatchers = new ArrayList<>();

			String[] patterns = ignorePatternsConfig.split("(\\s*,\\s*)|(\\s+)");
			for (String p : patterns) {
				ignorePatternMatchers.add(new AntPathMatcher(p.trim()));
			}
		}
	}

	private boolean ignoreURI(HttpServletRequest req) {

		if (ignorePatternMatchers == null)
			return false;

		String uri = WebUtils.parseRequestURI(req);

		for (AntPathMatcher matcher : ignorePatternMatchers) {
			if (matcher.matches(uri))
				return true;
		}

		return false;
	}
}
