package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationException;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;

public class StepReturnAction extends ProcessReturnAction {
	public static StepReturnAction getInstance(String stepId) {
		return new StepReturnAction(stepId);
	}

	private final String stepId;
	
	private StepReturnAction(String stepId) {
		this.stepId = stepId;
	}

	/**
	 * @return the stepId
	 */
	public String getStepId() {
		return stepId;
	}

	@Override
	protected OperationStepDef getNextStep(OperationDef operationDef, OperationStepDef stepDef) {
		for (OperationStepDef def : operationDef.getOpSteps()) {
			if (def.getId().equals(stepId)) {
				return def;
			}
		}

		throw new OperationException(
				"Operation[" + operationDef.getUri() + "] Step of [" + stepId + "] not found");
	}

	@Override
	public String toString() {
		return "step " + stepId;
	}
}

