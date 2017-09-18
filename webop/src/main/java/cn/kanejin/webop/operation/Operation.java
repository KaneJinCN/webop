package cn.kanejin.webop.operation;

import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;

import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.kanejin.webop.Constants;
import cn.kanejin.webop.MultipartFormdataRequest;
import cn.kanejin.webop.interceptor.InterceptorChainImpl;
import cn.kanejin.webop.interceptor.InterceptorMapping;
import cn.kanejin.webop.operation.action.EndReturnAction;
import cn.kanejin.webop.operation.action.NextReturnAction;
import cn.kanejin.webop.operation.action.ReturnAction;
import cn.kanejin.webop.operation.action.StepReturnAction;

/**
 * @version $Id: Operation.java 167 2017-09-13 09:26:47Z Kane $
 * @author Kane Jin
 */
public class Operation implements Serializable {
	private static final Logger log = LoggerFactory.getLogger(Operation.class);

	private final String uri;
	private final String name;
	private final List<String> interceptorRefs;
	private final List<OperationStepSpec> opSteps;
	
	public Operation(String id, String name, List<OperationStepSpec> opSteps) {
		this(id, name, null, opSteps);
	}

	public Operation(String uri, String name, List<String> interceptorRefs, List<OperationStepSpec> opSteps) {
		this.uri = uri;
		this.name = name;
		this.opSteps = opSteps;
		this.interceptorRefs = interceptorRefs;

		if (opSteps == null)
			return ;
	}

	public void operate(OperationContext context) throws ServletException, IOException {
		InterceptorChainImpl interceptorChain = new InterceptorChainImpl(this);
			
		if (interceptorRefs != null && !interceptorRefs.isEmpty()) {
			for (int i = 0; i < interceptorRefs.size(); i++)
				interceptorChain.addInterceptor(InterceptorMapping.getInstance().get(interceptorRefs.get(i)));
		}

		interceptorChain.intercept(context);
	}
	
	public void execute(OperationContext context) throws ServletException, IOException {
		EndReturnAction action = doExecute(context);
		
		if (action == null) {
			throw new ServletException("Operation [" + getUri() + "] is invalid, Please check your configuration!");
		}
		
		action.doAction(context);
	}

	@SuppressWarnings("unchecked")
	private EndReturnAction doExecute(OperationContext context) {
		log.info("Operation[{}] is being executed...", this.uri);
		
		if (log.isDebugEnabled()) {
			
			Enumeration<String> paramEnum = context.getRequest().getParameterNames();

			if (paramEnum.hasMoreElements())
				log.debug("%%%% Parameters in Context of Operation[{}]:", this.uri);

			while (paramEnum.hasMoreElements()) {
				String name = paramEnum.nextElement();
				if (name.endsWith("[]")) {
					String[] value = context.getRequest().getParameterValues(name);
					log.debug("%%%% [{}] = [{}]", name.substring(0, name.length() - 2), arrayToString(value));
				} else {
					String value = context.getRequest().getParameter(name);
					log.debug("%%%% [{}] = [{}]", name, value);
				}
			}
			
			Map<String, String> pathVars =
				(Map<String, String>) context.getRequest().getAttribute(Constants.PATH_VAR);
			
			if (pathVars != null) {

				log.debug("%%%% Path Variables in Context of Operation[{}]:", this.uri);

				for (String key : pathVars.keySet())
					log.debug("%%%% [{}] = [{}]", key, pathVars.get(key));
			}

			
			if (context.getRequest() instanceof MultipartFormdataRequest) {
				MultipartFormdataRequest mreq = (MultipartFormdataRequest) context.getRequest();

				Enumeration<String> fileEnum = mreq.getFileItemKeys();

				if (fileEnum.hasMoreElements())
					log.debug("%%%% FileItems in Context of Operation[{}]:", this.uri);

				while (fileEnum.hasMoreElements()) {
					String key = fileEnum.nextElement();
					FileItem item = context.getFileItem(key);
					log.debug("%%%% [{}] = [{}], size=[{}], contentType=[{}]",
						new Object[]{key, item.getName(), item.getSize(), item.getContentType()});
				}
			}
		}

		int i = 0, n = 0;
		while (true) {
			if (i < 0)
				throw new OperationException("Operation[" + uri + "] error");
			
			if (n++ > 100)
				throw new OperationException("Operation[" + uri + "] has too many steps");
		
			OperationStepSpec stepDef = this.opSteps.get(i); // 开始执行第一个Step
			OperationStep step = OperationStepFactory.getInstance().create(stepDef);

			log.info("Operation[{}] Step[{}] is being executed...", this.uri, stepDef.getId());

			int retValue = step.execute(context);

			log.info("Operation[{}] Step[{}] has been done, return value = [{}]",
					 new Object[]{this.uri, stepDef.getId(), retValue});

			ReturnAction retAction = stepDef.getReturnAction(String.valueOf(retValue));
			if (retAction == null) {
				retAction = stepDef.getReturnAction("-");
			}
			if (retAction == null) {
				throw new OperationException(
						"Operation[" + uri + "] Step[" + stepDef.getId() + 
						"] : No correct return-action is found");
			}
			
			// 如果是终结的返回动作，则退出
			if (retAction instanceof EndReturnAction) {
				log.info("Operation[{}] has been done, return action = [{}]", uri, retAction);
				return (EndReturnAction) retAction;
			}
			
			if (retAction instanceof NextReturnAction) {
				i += 1;
				if (i >= this.opSteps.size()) {
					throw new OperationException(
								"Operation[" + uri + "] Step[" + stepDef.getId() + 
								"] : return-action [next] is invalid");
				}
				continue;
			}

			if (retAction instanceof StepReturnAction) {
				String stepId = ((StepReturnAction)retAction).getStepId();
				i = indexOfStep(stepId);
				if (i < 0) {
					throw new OperationException(
								"Operation[" + uri + "] Step[" + stepId + 
								"] not found");
				}
				
				continue;
			}
		}
	}
	
	private int indexOfStep(String stepId) {
		for (int i = 0; i < opSteps.size(); i++) {
			if (opSteps.get(i).getId().equals(stepId))
				return i;
		}
		return -1;
	}

	public String getUri() {
		return uri;
	}

	public String getName() {
		return name;
	}

	public List<OperationStepSpec> getOpSteps() {
		return opSteps;
	}
	
	private String arrayToString(String[] src) {
		if (src == null || src.length == 0)
			return "";
		String result = src[0];
		for (int i = 1; i < src.length; i++) {
			result += "," + src[i];
		}
		return result;
	}
	
	@Override
	public String toString() {
		return this.uri;
	}
	
	@Override
	public int hashCode() {
		return this.uri.hashCode();
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
		return this.uri.equals(op.uri);
	}
}
