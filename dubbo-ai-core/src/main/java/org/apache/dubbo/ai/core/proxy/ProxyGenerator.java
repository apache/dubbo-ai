package org.apache.dubbo.ai.core.proxy;



import net.bytebuddy.ByteBuddy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
/**
 * @author liuzhifei
 * @since 1.0
 */
public class ProxyGenerator {

    public static <T> T createProxy(Class<T> interfaceClass) {
        try {
            // 使用 ByteBuddy 创建代理类
            Class<?> dynamicType = new ByteBuddy()
                    .subclass(Object.class)
                    .implement(interfaceClass)
                    .method(ElementMatchers.isDeclaredBy(interfaceClass))
                    .intercept(MethodDelegation.to(new AiServiceInterfaceImpl(interfaceClass)))
                    .make()
                    .load(interfaceClass.getClassLoader())
                    .getLoaded();

            return (T) dynamicType.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create proxy instance", e);
        }
    }
}
