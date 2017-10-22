package cn.kanejin.webop;

import cn.kanejin.webop.core.Operation;
import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.core.def.MultipartDef;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

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

		setEncoding(req, res);

		Operation op = WebopContext.get().getOperationMapping().get(req);

		if(op == null) {
			log.debug("No operation found: URI [{}] Method [{}]", req.getRequestURI(), req.getMethod());
			chain.doFilter(req, res);
		} else {
			log.info("URI = [{}], Method = [{}], Operation URI = [{}]",
					req.getRequestURI(), req.getMethod(), op.getUri());

			log.trace("User-Agent = [{}]", req.getHeader("User-Agent"));

			doOperation(wrapMultipartRequest(op.getMultipartDef(), req), res, op);
		}
	}

	private void setEncoding(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		String encoding = WebopContext.get().getWebopConfig().getCharset();
		if (isNotEmpty(encoding)) {
			req.setCharacterEncoding(encoding);
			res.setCharacterEncoding(encoding);
		}
	}

	private HttpServletRequest wrapMultipartRequest(MultipartDef multipartDef, HttpServletRequest req) {
		HttpServletRequest wrapper = req;

		if (multipartDef != null) {
			if (!(req instanceof MultipartRequestWrapper)) {
				if (ServletFileUpload.isMultipartContent(req)) {
					if (log.isDebugEnabled()) {
						log.debug("Wrapping HttpServletRequest with ContentType=\"{}\" into MultipartRequest",
								req.getContentType());
					}

					String encoding = WebopContext.get().getWebopConfig().getCharset();
					wrapper = new MultipartRequestWrapper(req, encoding, multipartDef.getMaxSize());
				}
			}
		}

		return wrapper;
	}

	@Override
	public void destroy() {}
}
