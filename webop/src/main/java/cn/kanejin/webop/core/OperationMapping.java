package cn.kanejin.webop.core;

import cn.kanejin.webop.cache.WebopCacheManager;
import cn.kanejin.webop.util.WebUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @version $Id: OperationMapping.java 168 2017-09-15 07:51:46Z Kane $
 * @author Kane Jin
 */
public class OperationMapping {
	private static final Logger log = LoggerFactory.getLogger(OperationMapping.class);

	private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+\\}");

	private static OperationMapping om;

	private Map<String, Operation> ops = new HashMap<String, Operation>();

	private List<String> oPatterns = new ArrayList<String>();

	private OperationMapping() {}

	public static OperationMapping getInstance() {
		if (om == null)
			om = new OperationMapping();
		return om;
	}

	public Operation get(String uri) {
		return ops.get(uri);
	}

	public void put(String uri, Operation op) {
		ops.put(uri, op);

		if (VARIABLE_PATTERN.matcher(uri).find())
			oPatterns.add(uri);
	}

	public Operation getOperation(HttpServletRequest req) {
		String uri = WebUtils.parseRequestURI(req);

		// Direct match
		Operation op = ops.get(uri);
		if (op != null)
			return op;

		Cache<String, PatternOperation> cache =
				WebopCacheManager.getInstance().getPatternOperationCache();

		// Pattern match
		Map<String, String> pVars = null;
		PatternOperation po = cache.get(uri);
		// retrieve from cache
		if (po != null) {
			op = po.getOperation();
			pVars = po.getPathVariables();
		}
		// have no cache
		else {
			List<String> matchingPatterns = new ArrayList<String>();
			for (String patt : oPatterns) {
				if (match(patt, uri))
					matchingPatterns.add(patt);
			}
			if (matchingPatterns.isEmpty())
				return null;

			// TODO multi pattern match?
			String opId = matchingPatterns.get(0);

			op = ops.get(opId);

			if (op != null) {
				pVars = parsePathVariables(op.getUri(), uri);

				cache.put(uri, new PatternOperation(op, pVars));
			}
		}

		if (op != null && pVars != null)
			req.setAttribute(Constants.PATH_VAR, pVars);

		return op;
	}

	private boolean match(String pattern, String uri) {
		if (pattern == null || pattern.isEmpty() || uri == null || uri.isEmpty())
			return false;

		String[] patSplit = pattern.split("/");
		String[] uriSplit = uri.split("/");

		if (patSplit.length != uriSplit.length)
			return false;

		for (int i = 0; i < patSplit.length; i++) {
			String p = patSplit[i];
			String u = uriSplit[i];
			if (p.startsWith("{") && p.endsWith("}")) {
			} else {
				if (!p.equals(u))
					return false;
			}
		}

		return true;
	}

	private Map<String, String> parsePathVariables(String pattern, String uri) {
		Map<String, String> result = new HashMap<String, String>();

		String[] patSplit = pattern.split("/");
		String[] uriSplit = uri.split("/");

		for (int i = 0; i < patSplit.length; i++) {
			String p = patSplit[i];
			String u = uriSplit[i];
			if (p.startsWith("{") && p.endsWith("}")) {
				result.put(p.substring(1, p.length() - 1), u);
			}
		}
		return result;
	}
}

