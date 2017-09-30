package cn.kanejin.webop;

import cn.kanejin.webop.cache.CachedResponse;
import cn.kanejin.webop.core.Operation;
import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.core.def.CacheDef;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public class DispatcherFilter extends IgnoreUriFilter {
	private static final Logger log = LoggerFactory.getLogger(DispatcherFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);
	}

	@Override
	public void doFilterInternal(HttpServletRequest req,
								 HttpServletResponse res,
								 FilterChain chain)
			throws IOException, ServletException {

		if (ignoreURI(req)) {
			log.trace("Ignore request[{}]", req.getRequestURI());
			chain.doFilter(req, res);

			return ;
		}

		Operation op = WebopContext.get().getOperationMapping().get(req);

		if(op == null) {
			log.debug("No operation found: URI [{}] Method [{}]", req.getRequestURI(), req.getMethod());
			chain.doFilter(req, res);
		} else {
			log.info("URI = [{}], Method = [{}], Operation URI = [{}]", req.getRequestURI(), req.getMethod(), op.getUri());
			log.trace("User-Agent = [{}]", req.getHeader("User-Agent"));

			if (op.needCached()) {
				String key = generateCacheKey(op.getCacheDef(), req);

				Cache<String, CachedResponse> cache =
						WebopContext.get().getCacheManager().getHttpResponseCache();

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
				OperationContext ctx = new OperationContextImpl(req, res, getServletContext());
				op.operate(ctx);
			}
		}
	}

	private CachedResponse createCachedResponseElement(
			HttpServletRequest req, HttpServletResponse res, Operation op)
			throws ServletException, IOException {

		final ByteArrayResponseWrapper wrapper = new ByteArrayResponseWrapper(res);

		OperationContext ctx = new OperationContextImpl(req, wrapper, getServletContext());
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

}
