package cn.kanejin.webop.operation.action;

public class NextReturnAction extends ProcessReturnAction {
	private static NextReturnAction action;
	
	public static NextReturnAction getInstance() {
		if (action == null)
			action = new NextReturnAction();
		
		return action;
	}
	
	private NextReturnAction() {}

	@Override
	public String toString() {
		return "next";
	}

}
