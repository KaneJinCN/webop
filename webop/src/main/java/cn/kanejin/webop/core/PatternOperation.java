package cn.kanejin.webop.core;

import java.io.Serializable;
import java.util.Map;

public class PatternOperation implements Serializable {
	private final String operationUri;

	private final Map<String, String> pathVariables;

	public PatternOperation(String operationUri, Map<String, String> pVars) {
		this.operationUri = operationUri;
		this.pathVariables = pVars;
	}

	public String getOperationUri() {
		return operationUri;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}
}
