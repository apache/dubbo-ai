package org.apache.dubbo.ai.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author liuzhifei
 * @since 1.0
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE,TYPE})
@Retention(RUNTIME)
@Documented
public @interface Prompt {
    String type() default "user";
    String value();
}
