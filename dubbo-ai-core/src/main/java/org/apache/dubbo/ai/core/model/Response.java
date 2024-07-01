package org.apache.dubbo.ai.core.model;

/**
 * @author liuzhifei
 * @since 1.0
 */
public interface Response<T> {
    
    T getContent();
    
    
}
