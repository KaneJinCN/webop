package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.OperationDef;
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

	private final Map<String, Operation> operations;

	private final List<PathVarURI> pathVarURIs;

	public OperationMapping() {
		operations = new HashMap<>();
		pathVarURIs = new ArrayList<>();
	}

	public void put(String uri, String[] methods, OperationDef opDef) {
		Operation op = new Operation(opDef);

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

	public Operation get(HttpServletRequest req) {
		String uri = WebUtils.parseRequestURI(req);
		String method = req.getMethod();

		// Direct match
		Operation op = lookupInOperations(uri, method);

		// 如果没找到，则在PatternOperation中匹配
		if (op == null) {
			op = lookupInPatternOperations(req);
		}

		return op;
	}

	private Operation lookupInOperations(String uri, String method) {
		Operation op = operations.get(uri);
		if (op == null) {
			op = operations.get(generateOperationId(uri, method));
		}

		return op;
	}

	private Operation lookupInPatternOperations(HttpServletRequest req) {
		String uri = WebUtils.parseRequestURI(req);
		String method = req.getMethod();

		Cache<String, PatternOperation> cache =
				WebopContext.get().getCacheManager().getPatternOperationCache();

		Operation operation = null;

		// Pattern match
		Map<String, String> pathVars = null;
		PatternOperation patternOperation = cache.get(uri);
		// retrieve from cache
		if (patternOperation != null) {
			operation = lookupInOperations(patternOperation.getUriPattern(), method);
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

			operation = lookupInOperations(firstPathVarURI.uri, method);

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

