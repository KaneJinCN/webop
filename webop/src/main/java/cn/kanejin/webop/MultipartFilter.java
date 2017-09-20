package cn.kanejin.webop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

/**
 * @author Kane Jin
 */
public class MultipartFilter implements Filter {
	private static final Logger log = LoggerFactory.getLogger(MultipartFilter.class);
	
	private static final long ONE_KB = 1024L;
	private static final long ONE_MB = ONE_KB * ONE_KB;
	private static final long ONE_GB = ONE_MB * ONE_KB;


	private String repositoryPath = System.getProperty("java.io.tmpdir");
	private long maxSize = ONE_MB;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		String repositoryPath = filterConfig.getInitParameter("uploadRepositoryPath");
		
		if (repositoryPath != null) {
			File file = new File(repositoryPath);
			if (!file.exists())
				log.error("Given repository Path for {} {} doesn't exists",
						getClass().getName(), repositoryPath);
			else if (!file.isDirectory())
				log.error("Given repository Path for {} {} is not a directory",
						getClass().getName(), repositoryPath);
			else {
				this.repositoryPath = repositoryPath;
			}
		}
	    this.maxSize = getMaxSize(filterConfig.getInitParameter("uploadMaxFileSize"));

	    log.info("Configure uploadRepositryPath to {}", this.repositoryPath);
	    log.info("Configure uploadMaxFileSize to  {}", this.maxSize);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
														throws IOException, ServletException {

		ServletRequest wrapper = req;
		
		if (req instanceof HttpServletRequest) {
			if (!(req instanceof MultipartRequestWrapper)) {
				String contentType = req.getContentType();
				if ((contentType != null)
						&& (contentType.toLowerCase(Locale.ENGLISH)
								.startsWith("multipart/form-data"))) {
					if (log.isDebugEnabled()) {
						log.debug("Wrapping {} with ContentType=\"{}\" into MultipartFormdataRequest",
								req.getClass().getName(), contentType);
					}

					wrapper = new MultipartRequestWrapper(
							(HttpServletRequest) req, this.repositoryPath,
							this.maxSize);
				}
			}
		}
		
		chain.doFilter(wrapper, res);
	}

	@Override
	public void destroy() {}
	
	private static long getMaxSize(String param) {
		if (param != null) {
			String number = param.toLowerCase(Locale.ENGLISH);
			long factor = 1L;
			if (number.endsWith("g")) {
				factor = ONE_GB;
				number = number.substring(0, number.length() - 1);
			} else if (number.endsWith("m")) {
				factor = ONE_MB;
				number = number.substring(0, number.length() - 1);
			} else if (number.endsWith("k")) {
				factor = ONE_KB;
				number = number.substring(0, number.length() - 1);
			}
			try {
				return Long.parseLong(number.trim()) * factor;
			} catch (NumberFormatException e) {
				log.error("Given max file size '{}' couldn't parsed to a number", param);
			}
		}
		return ONE_MB;
	}
}
