package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.operation.OperationContext;
import cn.kanejin.webop.operation.OperationStep;

public class ReturnActionStep implements OperationStep {

	@Override
	public int execute(OperationContext context) {
		if (context.getParameter("return") == null)
			return 0;

		return Integer.valueOf(context.getParameter("return"));
	}

}
