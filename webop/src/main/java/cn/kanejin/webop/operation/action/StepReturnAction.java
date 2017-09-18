package cn.kanejin.webop.operation.action;

public class StepReturnAction extends ProcessReturnAction {
	public static StepReturnAction getInstance(String stepId) {
		return new StepReturnAction(stepId);
	}

	private String stepId;
	
	private StepReturnAction(String stepId) {
		this.stepId = stepId;
	}

	/**
	 * @return the stepId
	 */
	public String getStepId() {
		return stepId;
	}

	/**
	 * @param stepId the stepId to set
	 */
	public void setStepId(String stepId) {
		this.stepId = stepId;
	}
	
	@Override
	public String toString() {
		return "step " + stepId;
	}
}
