package cn.kanejin.webop.core;

import java.io.Serializable;
import java.util.Map;

public class PatternOperation implements Serializable {
	private final String uriPattern;

	private final Map<String, String> pathVariables;

	public PatternOperation(String operationUri, Map<String, String> pVars) {
		this.uriPattern = operationUri;
		this.pathVariables = pVars;
	}

	public String getUriPattern() {
		return uriPattern;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}
}
