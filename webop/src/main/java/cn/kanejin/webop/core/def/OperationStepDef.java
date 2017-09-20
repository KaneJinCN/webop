package cn.kanejin.webop.core.def;

import cn.kanejin.webop.core.action.ReturnAction;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @version $Id: OperationStepDef.java 115 2016-03-15 06:34:36Z Kane $
 * @author Kane Jin
 */
public class OperationStepDef implements Serializable {
	private String id;

	private String clazz;

	private HashMap<String, ReturnAction> returnActions;

	private HashMap<String, String> initParams;

	public OperationStepDef() {
		this.returnActions = new HashMap<String, ReturnAction>();
		this.initParams = new HashMap<String, String>();
	}

	public OperationStepDef(String id, String className) {
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

	public ReturnAction getReturnAction(int returnValue) {

		ReturnAction retAction;
		if (returnActions.containsKey("always")) {
			retAction = returnActions.get("always");
		} else {
			retAction = returnActions.get(String.valueOf(returnValue));
			if (retAction == null) {
				retAction = returnActions.get("else");
			}
		}

		return retAction;
	}

	public void addReturnAction(String returnValueKey, ReturnAction returnAction) {
		this.returnActions.put(returnValueKey, returnAction);
	}

	@Override
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


	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;

		if (obj == this)
			return true;

		if (!(obj instanceof OperationStepDef)) {
			return false;
		}

		OperationStepDef def = (OperationStepDef) obj;

		return id.equals(def.id);
	}
}

