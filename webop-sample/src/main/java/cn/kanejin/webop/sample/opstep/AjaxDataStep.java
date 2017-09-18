package cn.kanejin.webop.sample.opstep;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.kanejin.webop.operation.OperationContext;
import cn.kanejin.webop.opstep.AbstractOperationStep;

public class AjaxDataStep extends AbstractOperationStep {

	private String name;
	
	@Override
	public void init(Map<String, String> params) {
		setName(params.get("name"));
	}

	@Override
	public int execute(OperationContext context) {
		context.setAttribute("name", name);
		
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("name", name);
		obj.put("ok", Boolean.TRUE);
		obj.put("word", "Hello");
		obj.put("date", new Date());
		context.setAttribute("ajaxData", obj);
		
		return 0;

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
