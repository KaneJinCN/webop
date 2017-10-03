/*
 * Copyright (c) 2017 Kane Jin
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package cn.kanejin.webop.core;

import cn.kanejin.webop.core.def.CacheDef;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;
import cn.kanejin.webop.core.exception.OperationException;
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
		LogHelper.logOperationParameters(log, this, context);

		executeStep(context, operationDef.getOpSteps().get(0), 0);
	}

	private void executeStep(OperationContext context, OperationStepDef stepDef, int stepCount)
			throws ServletException, IOException {

		if (stepCount > 100) {
			throw new OperationException(getUri(), stepDef.getId(),
					"There are too many steps, may be a infinite loop");
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
}
