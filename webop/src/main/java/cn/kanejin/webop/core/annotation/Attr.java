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
public @interface Attr {

    enum Scope {
        REQUEST,
        SESSION
    }

    String name();

    Scope scope() default Scope.REQUEST;
}
