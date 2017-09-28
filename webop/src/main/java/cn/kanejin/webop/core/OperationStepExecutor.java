package cn.kanejin.webop.core;

import cn.kanejin.commons.util.DateUtils;
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
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Date;

import static cn.kanejin.commons.util.StringUtils.*;

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

        Object[] stepParams = resolveStepParams(stepMethod, context);

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

                if (!method.getReturnType().isAssignableFrom(int.class)
                        && !method.getReturnType().isAssignableFrom(Integer.class)) {
                    throw new OperationException("@StepMethod's return type must be int or Integer");
                }

                return method;
            }
        }

        throw new OperationException("No @StepMethod found in the Step class");
    }


    /**
     * 分解组装StepMethod方法的参数列表
     *
     * @param stepMethod
     * @param context
     * @return
     */
    private Object[] resolveStepParams(Method stepMethod, OperationContext context) {

        Parameter[] params = stepMethod.getParameters();

        if (params == null || params.length == 0)
            return new Object[0];

        Object[] args = new Object[params.length];

        for (int i = 0; i < params.length; i++) {
            args[i] = resolveStepParam(params[i], context);
        }

        return args;
    }

    public Object resolveStepParam(Parameter p, OperationContext context) {
        if (p.getType().isAssignableFrom(OperationContext.class)) {
            return context;
        } else if (p.getType().isAssignableFrom(HttpServletRequest.class)) {
            return context.getRequest();
        } else if (p.getType().isAssignableFrom(HttpServletResponse.class)) {
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


    private Object resolveRequestParameter(Parameter p, OperationContext context) {
        Param ann = p.getAnnotation(Param.class);

        Object obj = null;
        if (p.getType().isAssignableFrom(FileItem.class)) { // 目标类型为上传文件

            obj = context.getFileItem(ann.name());

        } else if (p.getType().isAssignableFrom(Date.class)) { // 目标类型为日期
            obj = castDateParamType(
                    getParamValue(context.getRequest(), ann),
                    ann.pattern());
        } else if (p.getType().isArray()) { // 目标类型为Array

            obj = castArrayParamType(
                    getArrayParamValues(context.getRequest(), ann),
                    p.getType().getComponentType());

        } else if (Collection.class.isAssignableFrom(p.getType())) { // 目标类型为Collection

            throw new OperationException("@Param(name = \"" + ann.name() + "\") error: " +
                    "Collection Type is not supported, Please use Array instead");

        } else { // 目标类型为其它，使用参数为String的构造器转换
            obj = castGeneralParamType(
                    getParamValue(context.getRequest(), ann),
                    p.getType());
        }

        return obj;
    }

    private Object resolvePathVariable(Parameter p, OperationContext context) {
        PathVar ann = p.getAnnotation(PathVar.class);

        String pathVariableValue = getPathVariableValue(context, ann);

        Object obj = null;

        if (p.getType().isAssignableFrom(Date.class)) { // 目标类型为日期
            obj = castDateParamType(pathVariableValue, ann.pattern());

        } else { // 目标类型为其它，使用参数为String的构造器转换
            obj = castGeneralParamType(pathVariableValue, p.getType());
        }

        return obj;
    }

    /**
     * 解析没有注解的参数。
     *
     * <p>
     * 根据参数的真实名称，在Request中查找参数值，
     * 如果编译时参数的真实名称没有保持，则抛出异常
     *
     * @param p
     * @param context
     * @return
     */
    private Object resolveNoAnnParam(Parameter p, OperationContext context) {
        if (p.isNamePresent()) {
            return castGeneralParamType(
                    context.getRequest().getParameter(p.getName()),
                    p.getType());
        } else {
            throw new OperationException("Step Method parameter name is not specified");
        }
    }


    /**
     * 从Request中获取参数值，当为null或空时，根据Param注解的配置设置默认值
     */
    private String getParamValue(HttpServletRequest request, Param ann) {
        String paramValue = request.getParameter(ann.name());

        if (paramValue == null && isNotBlank(ann.ifNull())) {
            paramValue = ann.ifNull();
        }
        if (isEmpty(paramValue) && isNotBlank(ann.ifEmpty())) {
            paramValue = ann.ifEmpty();
        }

        return paramValue;
    }
    /**
     * 从Request中获取数组参数值，当为null或空时，根据Param注解的配置设置默认值
     */
    private String[] getArrayParamValues(HttpServletRequest request, Param ann) {
        String[] paramValues = request.getParameterValues(ann.name() + "[]");

        if (paramValues == null && isNotBlank(ann.ifNull())) {
            paramValues = new String[]{ann.ifNull()};
        }
        if (paramValues != null && paramValues.length == 0 && isNotBlank(ann.ifEmpty())) {
            paramValues = new String[]{ann.ifEmpty()};
        }

        return paramValues;
    }

    /**
     * 获取路径参数的值，当为null或空时，根据PathVar注解的配置设置默认值
     */
    private String getPathVariableValue(OperationContext context, PathVar ann) {
        String paramName = ann.name();
        String paramValue = context.getPathVariable(paramName);

        if (paramValue == null && isNotBlank(ann.ifNull())) {
            paramValue = ann.ifNull();
        }
        if (isEmpty(paramValue) && isNotBlank(ann.ifEmpty())) {
            paramValue = ann.ifEmpty();
        }

        return paramValue;
    }

    public Object resolveAttribute(Parameter p, OperationContext context) {
        Attr ann = p.getAnnotation(Attr.class);

        Object obj = null;

        switch (ann.scope()) {
            case REQUEST:
                obj = context.getRequest().getAttribute(ann.name());

                break;
            case SESSION:
                HttpSession session = context.getRequest().getSession(false);

                if (session != null) {
                    obj = session.getAttribute(ann.name());
                }

                break;

            default:
                break;
        }

        return obj;
    }

    /**
     * 把日期字符串根据指定的格式转换成Date类型
     *
     * @param paramValue 日期字符串
     * @param pattern 日期格式
     * @return 转换后的Date
     */
    private Date castDateParamType(String paramValue, String pattern) {
        if (isBlank(pattern)) {
            throw new OperationException("Date's pattern is required");
        }

        return DateUtils.parseDate(paramValue, pattern);
    }

    /**
     * 转换数组类型
     *
     * @param paramValues 字符串数组
     * @param paramComponentType 数据元素类型
     *
     * @return 转换后的数组
     */
    private <T> T[] castArrayParamType(String[] paramValues, Class<T> paramComponentType) {
        if (paramValues == null)
            return null;

        if (paramComponentType.isAssignableFrom(String.class)) {
            return (T[]) paramValues;
        }

        T[] result = (T[]) Array.newInstance(paramComponentType, paramValues.length);

        for (int i = 0; i < paramValues.length; i++) {
            result[i] = castGeneralParamType(paramValues[i], paramComponentType);
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
     * @return 转换类型后的值
     */
    private <T> T castGeneralParamType(String paramValue, Class<T> paramType) {
        if (paramValue == null)
            return null;

        if (paramType == null || paramType.isAssignableFrom(String.class)) {
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
                        && consParamTypes[0].isAssignableFrom(String.class)) {
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
