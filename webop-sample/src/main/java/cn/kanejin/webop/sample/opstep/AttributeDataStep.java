package cn.kanejin.webop.sample.opstep;

import cn.kanejin.webop.core.InitializableStep;
import cn.kanejin.webop.core.OperationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AttributeDataStep implements InitializableStep {

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
		context.setAttribute("attrData", obj);
		
		return 0;

	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
