package cn.kanejin.webop.core;

import cn.kanejin.webop.core.action.ReturnAction;
import cn.kanejin.webop.core.annotation.Attr;
import cn.kanejin.webop.core.annotation.Param;
import cn.kanejin.webop.core.annotation.PathVar;
import cn.kanejin.webop.core.annotation.StepMethod;
import cn.kanejin.webop.core.def.OperationDef;
import cn.kanejin.webop.core.def.OperationStepDef;
import org.apache.commons.fileupload.FileItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import static cn.kanejin.commons.util.StringUtils.isEmpty;
import static cn.kanejin.commons.util.StringUtils.isNotBlank;

/**
 * @author Kane Jin
 */
public class OperationStepExecutor {
    private static final Logger log = LoggerFactory.getLogger(OperationStepExecutor.class);

    private OperationStepExecutor() {}

    private static OperationStepExecutor executor;
    public static OperationStepExecutor getInstance() {
        if (executor == null)
            executor = new OperationStepExecutor();

        return executor;
    }

    public OperationStepDef execute(
            OperationContext context, OperationDef operationDef, OperationStepDef stepDef)
            throws ServletException, IOException {

        OperationStep step = OperationStepFactory.getInstance().create(stepDef);

        Method stepMethod = getStepMethod(step);

        Object[] stepParams = resolveParams(stepMethod, context);

        int retValue;
        try {
            retValue = (Integer) stepMethod.invoke(step, stepParams);
        } catch (Throwable e) {
            throw new OperationException(
                    "Operation[" + operationDef.getUri() + "] Step[" + stepDef.getId() +
                            "] Error", e);
        }

        log.info("Operation[{}] Step[{}] return value = [{}]",
                operationDef.getUri(), stepDef.getId(), retValue);

        ReturnAction retAction = stepDef.getReturnAction(retValue);

        if (retAction == null) {
            throw new OperationException(
                    "Operation[" + operationDef.getUri() + "] Step[" + stepDef.getId() +
                            "] : No correct return-action is found");
        }

        log.info("Operation[{}] Step[{}] return action = [{}]",
                operationDef.getUri(), stepDef.getId(), retAction);

        return retAction.doAction(operationDef, stepDef, context);
    }

    /**
     * 获取StepClass中声明为@StepMethod的方法。
     *
     * @param step
     * @return
     */
    private Method getStepMethod(OperationStep step) {
        Method[] methods = step.getClass().getDeclaredMethods();

        for (Method method : methods) {
            if (method.isAnnotationPresent(StepMethod.class)) {

                if (!method.getReturnType().equals(int.class)
                        && !method.getReturnType().equals(Integer.class)) {
                    throw new OperationException("@StepMethod's return type must be int or Integer");
                }

                return method;
            }
        }

        throw new OperationException("There is not a @StepMethod in the Step class");
    }


    /**
     * 分解组装StepMethod方法的参数列表
     *
     * @param stepMethod
     * @param context
     * @return
     */
    private Object[] resolveParams(Method stepMethod, OperationContext context) {

        Parameter[] params = stepMethod.getParameters();

        if (params == null || params.length == 0)
            return new Object[0];

        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            args[i] = resolveParam(params[i], context);
        }

        return args;
    }

    public Object resolveParam(Parameter p, OperationContext context) {
        if (p.getType().equals(OperationContext.class)) {
            return context;
        } else if (p.getType().equals(HttpServletRequest.class)) {
            return context.getRequest();
        } else if (p.getType().equals(HttpServletResponse.class)) {
            return context.getResponse();
        } else {
            if (p.isAnnotationPresent(Param.class)) {
                return resolveRequestParameter(p, context);
            } else if (p.isAnnotationPresent(PathVar.class)) {
                return resolvePathVariable(p, context);
            } else if (p.isAnnotationPresent(Attr.class)) {
                return resolveAttribute(p, context);
            } else {
                return resolveNoAnnParam(p, context);
            }
        }
    }

    private Object resolveNoAnnParam(Parameter p, OperationContext context) {
        if (p.isNamePresent()) {
            return castParamType(
                    context.getRequest().getParameter(p.getName()),
                    p.getType());

        } else {
            throw new OperationException("Step Method parameter name is not specified");
        }

    }

    private Object resolveRequestParameter(Parameter p, OperationContext context) {
        Param ann = p.getAnnotation(Param.class);

        String paramName = ann.name();

        Object obj = null;
        if (p.getType().equals(FileItem.class)) { // 如果是上传文件的情况

            obj = context.getFileItem(paramName);

        } else if (p.getType().isArray()) { // 如果是数组的情况

            String[] paramValues = context.getRequest().getParameterValues(paramName + "[]");

            if (paramValues == null && isNotBlank(ann.ifNull())) {
                paramValues = new String[]{ann.ifNull()};
            }
            if (paramValues != null && paramValues.length == 0 && isNotBlank(ann.ifEmpty())) {
                paramValues = new String[]{ann.ifEmpty()};
            }

            obj = castArrayParamType(paramValues, p.getType().getComponentType());

        } else {
            String paramValue = context.getRequest().getParameter(paramName);

            if (paramValue == null && isNotBlank(ann.ifNull())) {
                paramValue = ann.ifNull();
            }
            if (isEmpty(paramValue) && isNotBlank(ann.ifEmpty())) {
                paramValue = ann.ifEmpty();
            }

            obj = castParamType(paramValue, p.getType());
        }

        return obj;
    }

    private Object resolvePathVariable(Parameter p, OperationContext context) {
        PathVar ann = p.getAnnotation(PathVar.class);

        String paramName = ann.name();
        String paramValue = context.getPathVariable(paramName);

        if (paramValue == null && isNotBlank(ann.ifNull())) {
            paramValue = ann.ifNull();
        }
        if (isEmpty(paramValue) && isNotBlank(ann.ifEmpty())) {
            paramValue = ann.ifEmpty();
        }

        return castParamType(paramValue, p.getType());
    }

    public Object resolveAttribute(Parameter p, OperationContext context) {
        Attr ann = p.getAnnotation(Attr.class);

        return context.getAttribute(ann.name());
    }

    private <T> T[] castArrayParamType(String[] paramValues, Class<T> paramComponentType) {
        if (paramValues == null)
            return null;

        if (paramComponentType.equals(String.class)) {
            return (T[]) paramValues;
        }

        T[] result = (T[]) Array.newInstance(paramComponentType, paramValues.length);

        for (int i = 0; i < paramValues.length; i++) {
            result[i] = (T) castParamType(paramValues[i], paramComponentType);
        }

        return result;
    }

    /**
     * 当参数类型不是String, 需要把String转成参数所需的类型
     *
     * <p>
     * 转换是通过目标类型的构造器来完成。
     * 目标类型必须存在只有String作为参数的构造器Constructor(String),
     * 否则转换失败返回null
     *
     *
     * @param paramValue 参数值（字符串）
     * @param paramType 目标类型
     *
     * @return
     */
    private <T> T castParamType(String paramValue, Class<T> paramType) {
        if (paramValue == null)
            return null;

        if (paramType.equals(String.class)) {
            return (T) paramValue;
        }

        Constructor[] cons = paramType.getDeclaredConstructors();

        // Find constructor with single String argument
        Constructor stringConstructor = null;
        if (cons != null) {
            for (Constructor c : cons) {
                Class<?>[] consParamTypes = c.getParameterTypes();

                if (consParamTypes != null
                        && consParamTypes.length == 1
                        && consParamTypes[0].equals(String.class)) {
                    stringConstructor = c;
                    break;
                }
            }
        }

        if (stringConstructor == null) {
            log.warn("Constructor of " + paramType + " with single String argument is not found");
            return null;
        }

        try {
            return (T) stringConstructor.newInstance(paramValue);
        } catch (Throwable e) {
            log.warn("Create Instance of " + paramType + " from String \"" + paramValue + "\" Error", e);

            return null;
        }
    }

}
