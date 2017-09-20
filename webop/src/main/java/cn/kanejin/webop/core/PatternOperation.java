package cn.kanejin.webop.core;

import java.io.Serializable;
import java.util.Map;

public class PatternOperation implements Serializable {
	private Operation operation;

	private Map<String, String> pathVariables;

	public PatternOperation(Operation op, Map<String, String> pVars) {
		this.operation = op;
		this.pathVariables = pVars;
	}

	public Operation getOperation() {
		return operation;
	}

	public void setOperation(Operation operation) {
		this.operation = operation;
	}

	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	public void setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
	}
}
