package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;
import cn.kanejin.webop.core.exception.OperationException;

public class StepReturnAction extends ProcessReturnAction {
	public static StepReturnAction build(String stepId) {
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
	protected OperationStepDef nextStep(OperationDef operationDef, OperationStepDef stepDef) {
		for (OperationStepDef def : operationDef.getOpSteps()) {
			if (def.getId().equals(stepId)) {
				return def;
			}
		}

		throw new OperationException(operationDef.getUri(), stepId, "Step is not found");
	}

	@Override
	public String toString() {
		return "step " + stepId;
	}
}

