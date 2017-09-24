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

import static cn.kanejin.commons.util.StringUtils.isBlank;

/**
 * @author Kane Jin
 */
public class OperationMapping {
	private static final Logger log = LoggerFactory.getLogger(OperationMapping.class);

	private static OperationMapping om;

	private Map<String, Operation> operations = new HashMap<>();

	private List<PathVarURI> pathVarURIs = new ArrayList<>();

	private OperationMapping() {}

	public static OperationMapping getInstance() {
		if (om == null)
			om = new OperationMapping();
		return om;
	}

	public void put(String uri, String[] methods, Operation op) {
		if (methods == null || methods.length == 0) {
			methods = new String[]{""};
		}

		for (String m : methods) {
			operations.put(generateOperationId(uri, m), op);
		}

		if (PathVarURI.isPathVarURI(uri))
			pathVarURIs.add(new PathVarURI(uri));
	}


	public boolean exists(String uri, String method) {
		return operations.containsKey(generateOperationId(uri, method));
	}


	public Operation getOperation(HttpServletRequest req) {
		String uri = WebUtils.parseRequestURI(req);
		String method = req.getMethod();

		// Direct match
		Operation op = findInOperations(uri, method);

		// 如果没找到，则在PatternOperation中匹配
		if (op == null) {
			op = findInPatternOperation(req);
		}

		return op;
	}

	private Operation findInOperations(String uri, String method) {
		Operation op = operations.get(uri);
		if (op == null) {
			op = operations.get(generateOperationId(uri, method));
		}

		return op;
	}

	private Operation findInPatternOperation(HttpServletRequest req) {
		String uri = WebUtils.parseRequestURI(req);
		String method = req.getMethod();

		Cache<String, PatternOperation> cache =
				WebopCacheManager.getInstance().getPatternOperationCache();

		Operation operation = null;

		// Pattern match
		Map<String, String> pathVars = null;
		PatternOperation patternOperation = cache.get(uri);
		// retrieve from cache
		if (patternOperation != null) {
			operation = findInOperations(patternOperation.getUriPattern(), method);
			pathVars = patternOperation.getPathVariables();
		}
		// have no cache
		else {
			List<PathVarURI> matchedPathVarURIs = new ArrayList<>();
			for (PathVarURI pathVarURI : pathVarURIs) {
				if (pathVarURI.matches(uri))
					matchedPathVarURIs.add(pathVarURI);
			}
			if (matchedPathVarURIs.isEmpty())
				return null;

			PathVarURI firstPathVarURI = matchedPathVarURIs.get(0);

			operation = findInOperations(firstPathVarURI.uri, method);

			if (operation != null) {
				pathVars = firstPathVarURI.extractPathVariables(uri);

				cache.put(uri, new PatternOperation(operation.getUri(), pathVars));
			}
		}

		if (operation != null && pathVars != null)
			req.setAttribute(Constants.PATH_VAR, pathVars);

		return operation;
	}

	private String generateOperationId(String uri, String method) {
		return isBlank(method)
				? uri
				: method + "_" + uri;
	}


}

