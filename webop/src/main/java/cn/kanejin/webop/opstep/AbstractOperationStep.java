package cn.kanejin.webop.opstep;

import java.util.Map;

import cn.kanejin.webop.operation.OperationStep;

public abstract class AbstractOperationStep implements OperationStep {
	public void init(Map<String, String> params) {}
}
