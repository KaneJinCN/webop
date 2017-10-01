package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.CacheDef;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @version $Id: Operation.java 167 2017-09-13 09:26:47Z Kane $
 * @author Kane Jin
 */
public class Operation implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(Operation.class);

	private final OperationDef operationDef;

	private List<Interceptor> interceptors;

	public Operation(OperationDef operationDef) {
		this.operationDef = operationDef;
	}

	public void operate(OperationContext context) throws ServletException, IOException {
		if (operationDef.hasInterceptors()) {
			// 如果有拦截器，先组装拦截器列表，再创建拦截链并开始执行
			if (interceptors == null) {
				interceptors = new ArrayList<>();
				for (String id : operationDef.getInterceptorRefs()) {
					interceptors.add(WebopContext.get().getInterceptorMapping().get(id));
				}
			}

			InterceptorChain interceptorChain =
					new InterceptorChainImpl(interceptors, cxt -> execute(cxt));

			interceptorChain.intercept(context);
		} else {
			// 如果没有拦截器，则直接执行Operation
			this.execute(context);
		}
	}

	public boolean needCached() {
		return operationDef.getCacheDef() != null;
	}

	public CacheDef getCacheDef() {
		return operationDef.getCacheDef();
	}

	public void execute(OperationContext context) throws ServletException, IOException {
		log.info("Operation[{}] is being executed...", getUri());
		logOperationParameters(context);

		executeStep(context, operationDef.getOpSteps().get(0), 0);
	}

	private void executeStep(OperationContext context, OperationStepDef stepDef, int stepCount)
			throws ServletException, IOException {

		if (stepCount > 100) {
			throw new OperationException("Operation[" + getUri() + "] has too many steps");
		}

		OperationStepDef nextStepDef =
				OperationStepExecutor.getInstance().execute(context, operationDef, stepDef);

		// 如果没有下一个Step，退出递归
		if (nextStepDef == null) {
			return ;
		}

		// 如果有下一个Step，递归继续执行Step
		executeStep(context, nextStepDef, ++stepCount);
	}

	public String getUri() {
		return operationDef.getUri();
	}

	public String getName() {
		return operationDef.getName();
	}

	@Override
	public String toString() {
		return getUri();
	}
	
	@Override
	public int hashCode() {
		return getUri().hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Operation))
			return false;
		
		Operation op = (Operation) obj;
		return this.getUri().equals(op.getUri());
	}

	private void logOperationParameters(OperationContext context) {
		if (log.isDebugEnabled()) {

			Enumeration<String> paramEnum = context.getRequest().getParameterNames();

			if (paramEnum.hasMoreElements())
				log.debug("%%%% Parameters in Context of Operation[{}]:", getUri());

			while (paramEnum.hasMoreElements()) {
				String name = paramEnum.nextElement();
				if (name.endsWith("[]")) {
					String[] value = context.getRequest().getParameterValues(name);
					log.debug("%%%% [{}] = {}", name.substring(0, name.length() - 2), Arrays.toString(value));
				} else {
					String value = context.getRequest().getParameter(name);
					log.debug("%%%% [{}] = [{}]", name, value);
				}
			}

			Map<String, String> pathVars =
					(Map<String, String>) context.getRequest().getAttribute(Constants.PATH_VAR);

			if (pathVars != null) {

				log.debug("%%%% Path Variables in Context of Operation[{}]:", getUri());

				for (String key : pathVars.keySet())
					log.debug("%%%% [{}] = [{}]", key, pathVars.get(key));
			}


			if (context.isMultipart()) {
				Enumeration<String> fileEnum = context.getFileItemKeys();

				if (fileEnum.hasMoreElements())
					log.debug("%%%% FileItems in Context of Operation[{}]:", getUri());

				while (fileEnum.hasMoreElements()) {
					String key = fileEnum.nextElement();
					FileItem item = context.getFileItem(key);
					log.debug("%%%% [{}] = [{}], size=[{}], contentType=[{}]",
							new Object[]{key, item.getName(), item.getSize(), item.getContentType()});
				}
			}
		}
	}

}
