package cn.kanejin.webop;

import cn.kanejin.webop.core.Operation;
import cn.kanejin.webop.core.WebopContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static cn.kanejin.commons.util.StringUtils.isNotEmpty;

/**
 * @author Kane Jin
 */
public class DispatcherFilter extends OperationSupportFilter {
	private static final Logger log = LoggerFactory.getLogger(DispatcherFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
								 HttpServletResponse res,
								 FilterChain chain)
			throws IOException, ServletException {

		String encoding = WebopContext.get().getWebopConfig().getCharset();
		if (isNotEmpty(encoding)) {
			req.setCharacterEncoding(encoding);
			res.setCharacterEncoding(encoding);
		}

		Operation op = WebopContext.get().getOperationMapping().get(req);

		if(op == null) {
			log.debug("No operation found: URI [{}] Method [{}]", req.getRequestURI(), req.getMethod());
			chain.doFilter(req, res);
		} else {
			log.info("URI = [{}], Method = [{}], Operation URI = [{}]", req.getRequestURI(), req.getMethod(), op.getUri());
			log.trace("User-Agent = [{}]", req.getHeader("User-Agent"));

			doOperation(req, res, op);
		}
	}

	@Override
	public void destroy() {}
}
