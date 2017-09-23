package cn.kanejin.webop;

import cn.kanejin.webop.support.AntPathMatcher;
import cn.kanejin.webop.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public abstract class IgnoreUriFilter extends ServletContextSupport {
	private static final Logger log = LoggerFactory.getLogger(IgnoreUriFilter.class);

	private List<String> ignorePatterns;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

		super.init(filterConfig);

		initIgnorePatterns(filterConfig.getInitParameter("ignoreUriPatterns"));
	}

	private void initIgnorePatterns(String ignorePatternsConfig) {
		if (isNotBlank(ignorePatternsConfig)) {
			ignorePatterns = new ArrayList<String>();

			String[] patterns = ignorePatternsConfig.split("(\\s*,\\s*)|(\\s+)");
			for (String p : patterns) {
				ignorePatterns.add(p.trim());
			}
		}
	}

	protected boolean ignoreURI(HttpServletRequest req) {

		if (ignorePatterns == null)
			return false;

		String uri = WebUtils.parseRequestURI(req);

		for (String pattern : ignorePatterns) {
			if (AntPathMatcher.matches(pattern, uri))
				return true;
		}

		return false;
	}

}
