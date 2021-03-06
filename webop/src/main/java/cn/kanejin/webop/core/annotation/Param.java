package cn.kanejin.webop.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Kane Jin
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface Param {
    String name();

    String ifNull() default "";

    String ifEmpty() default "";

    /**
     * 当数据目标类型为Date时，可以指定Format格式
     */
    String pattern() default "";
}
