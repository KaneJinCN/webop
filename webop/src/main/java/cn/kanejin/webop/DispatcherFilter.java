package cn.kanejin.webop;

import cn.kanejin.webop.core.def.CacheDef;
import cn.kanejin.webop.cache.CachedResponse;
import cn.kanejin.webop.cache.WebopCacheManager;
import cn.kanejin.webop.core.*;
import cn.kanejin.webop.support.AntPathMatcher;
import cn.kanejin.webop.util.WebUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
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

			if (op.needCached()) {
				String key = generateCacheKey(op.getCacheDef(), req);

				Cache<String, CachedResponse> cache = WebopCacheManager.getInstance().getHttpResponseCache();

				CachedResponse cachedResponse = cache.get(key);

				if (cachedResponse == null) {
					cachedResponse = createCachedResponseElement(req, res, op);

					if (cachedResponse.isOk()) {
						cache.put(key, cachedResponse);
					}
				}

				if (cachedResponse.isOk()) {
					writeToResponse(res, cachedResponse);
				}

			} else {
				OperationContext ctx = new OperationContextImpl(req, res, servletContext);
				op.operate(ctx);
			}
		}
	}

	private CachedResponse createCachedResponseElement(
			HttpServletRequest req, HttpServletResponse res, Operation op)
			throws ServletException, IOException {

		final ByteArrayResponseWrapper wrapper = new ByteArrayResponseWrapper(res);

		OperationContext ctx = new OperationContextImpl(req, wrapper, servletContext);
		op.operate(ctx);

		wrapper.flush();

		CachedResponse element = new CachedResponse(op.getCacheDef().getExpiryDef());
		element.setResponse(wrapper.getStatusCode(), wrapper.getContentType(), wrapper.toByteArray());

		return element;
	}

	private void writeToResponse(HttpServletResponse res,
								 CachedResponse cachedResponse) throws IOException {

		res.setStatus(cachedResponse.getStatusCode());

		if (isNotBlank(cachedResponse.getContentType())) {
			res.setContentType(cachedResponse.getContentType());
		}

		byte[] bodyContent = cachedResponse.getContent();
		res.setContentLength(bodyContent.length);
		OutputStream out = new BufferedOutputStream(res.getOutputStream());
		out.write(bodyContent);
		out.flush();
	}

	private String generateCacheKey(CacheDef cacheDef, HttpServletRequest req) {
		if (cacheDef == null)
			return null;

		StringBuilder key = new StringBuilder(req.getMethod());
		key.append('_');
		key.append(req.getRequestURI());

		for (String fieldName : cacheDef.getKeyFields()) {
			key.append('_');
			key.append(fieldName);
			key.append('=');
			key.append(req.getParameter(fieldName));
		}

		return key.toString();
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

		String uri = WebUtils.parseRequestURI(req);

		for (String pattern : ignorePatterns) {
			if (AntPathMatcher.matches(pattern, uri))
				return true;
		}

		return false;
	}

}
