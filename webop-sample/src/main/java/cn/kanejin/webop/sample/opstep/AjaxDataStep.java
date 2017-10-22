package cn.kanejin.webop.sample.opstep;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cn.kanejin.webop.core.InitializableStep;
import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.annotation.StepMethod;

public class AjaxDataStep implements InitializableStep {

	private String name;
	
	@Override
	public void init(Map<String, String> params) {
		setName(params.get("name"));
	}

	@StepMethod
	public int execute(OperationContext context) {
		context.setAttribute("name", name);
		
		Map<String, Object> obj = new HashMap<String, Object>();
		obj.put("name", name);
		obj.put("ok", Boolean.TRUE);
		obj.put("word", "你好");
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
