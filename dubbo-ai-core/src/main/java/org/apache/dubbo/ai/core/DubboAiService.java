package org.apache.dubbo.ai.core;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.List;

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
public @interface DubboAiService {

    /**
     * dubbo.ai.m1.provider = openai
     * dubbo.ai.m1.sk = sk-xxxxxxxx
     * dubbo.ai.m1.baseurl = https://api.openai.com
     * you can add more provider and will load balance and fail back to try another
     * if not set will get the config dubbo.ai.%s(modelCompany).providers
     */
    String[] modelProvider() default "";
    
    String modelCompany() default "openai";
    
    String model() default "gpt-4o";
    
    String configPath() default "application.yml";
    
}
