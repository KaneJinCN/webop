package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.OperationStep;

public class ReturnActionStep implements OperationStep {

	@Override
	public int execute(OperationContext context) {
		if (context.getParameter("return") == null)
			return 0;

		return Integer.valueOf(context.getParameter("return"));
	}

}
