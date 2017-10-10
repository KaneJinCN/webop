package cn.kanejin.webop.core.action;

import cn.kanejin.webop.core.OperationContext;
import cn.kanejin.webop.core.WebopContext;
import cn.kanejin.webop.core.view.ViewRenderer;

import javax.servlet.ServletException;
import java.io.IOException;

public class ForwardReturnAction extends EndReturnAction {
	
	public static ForwardReturnAction build(String type, String page) {
		return new ForwardReturnAction(type, page);
	}

	private final String type;
	private final String page;
	
	private ForwardReturnAction(String type, String page) {
		this.type = type;
		this.page = page;
	}

	@Override
	public void doActionInternal(OperationContext oc) throws ServletException, IOException {
		ViewRenderer renderer = null;

		if ("jsp".equalsIgnoreCase(type)) {
			renderer = WebopContext.get().getWebopConfig().getJspViewRenderer();
		} else if ("freemarker".equalsIgnoreCase(type)) {
			renderer = WebopContext.get().getWebopConfig().getFreemarkerViewRenderer();
		} else {
			throw new UnsupportedOperationException("view type '" + type + "' is not supported");
		}

		renderer.render(page, oc.getRequest(), oc.getResponse());
	}

	/**
	 * @return the page
	 */
	public String getPage() {
		return page;
	}

	@Override
	public String toString() {
		return "forward " + page;
	}
}
