package cn.kanejin.webop.operation;

import java.util.HashMap;

import cn.kanejin.webop.operation.action.ReturnAction;

/**
 * @version $Id: OperationStepSpec.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class OperationStepSpec {
	private String id;

	private String clazz;

	private HashMap<String, ReturnAction> returnActions;

	private HashMap<String, String> initParams;

	public OperationStepSpec() {
		this.returnActions = new HashMap<String, ReturnAction>();
		this.initParams = new HashMap<String, String>();
	}

	public OperationStepSpec(String id, String className) {
		this.id = id;
		this.setClazz(className);
		this.returnActions = new HashMap<String, ReturnAction>();
		this.initParams = new HashMap<String, String>();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ReturnAction getReturnAction(String returnValue) {
		return (ReturnAction) this.returnActions.get(returnValue);
	}

	public void addReturnAction(String returnValue, ReturnAction returnAction) {
		this.returnActions.put(returnValue, returnAction);
	}

	public String toString() {
		StringBuilder localStringBuffer = new StringBuilder();
		localStringBuffer.append("<step id=\"" + getId() + "\" ");
		localStringBuffer.append("class=\"" + getClazz() + "\" ");
		localStringBuffer.append("/>");
		return localStringBuffer.toString();
	}

	public void addInitParam(String paramName, String paramVlaue) {
		this.initParams.put(paramName, paramVlaue);
	}

	public HashMap<String, String> getInitParams() {
		return this.initParams;
	}

	public String getInitParam(String paramName) {
		return this.initParams.get(paramName);
	}

	/**
	 * @return the clazz
	 */
	public String getClazz() {
		return clazz;
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(String clazz) {
		this.clazz = clazz;
	}
}
