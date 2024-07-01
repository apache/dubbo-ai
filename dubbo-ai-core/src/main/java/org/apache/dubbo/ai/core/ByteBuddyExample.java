//package org.apache.dubbo.ai.core;
//
///**
// * @author liuzhifei
// * @since 1.0
// */
//import net.bytebuddy.ByteBuddy;
//import net.bytebuddy.implementation.MethodDelegation;
//import net.bytebuddy.implementation.bind.annotation.AllArguments;
//import net.bytebuddy.implementation.bind.annotation.Origin;
//import net.bytebuddy.implementation.bind.annotation.RuntimeType;
//import net.bytebuddy.implementation.bind.annotation.SuperCall;
//import net.bytebuddy.matcher.ElementMatchers;
//
//import java.lang.reflect.Method;
//import java.util.concurrent.Callable;
//
//public class ByteBuddyExample {
//    public static void main(String[] args) throws Exception {
//        // 定义要代理的接口
//        Class<?> interfaceToProxy = MyInterface.class;
//
//        // 使用 ByteBuddy 创建代理类
//        Class<?> dynamicType = new ByteBuddy()
//                .subclass(Object.class)
//                .implement(interfaceToProxy)
//                .method(ElementMatchers.isDeclaredBy(interfaceToProxy))
//                .intercept(MethodDelegation.to(MyInterfaceImpl.class))
//                .make()
//                .load(ByteBuddyExample.class.getClassLoader())
//                .getLoaded();
//
//        // 创建代理实例
//        MyInterface proxy = (MyInterface) dynamicType.getDeclaredConstructor().newInstance();
//        // 调用接口方法
//        proxy.myMethod();
//    }
//    // 实现接口的类
//    public static class MyInterfaceImpl {
//        @RuntimeType
//        public static Object intercept( @Origin Method method) throws Exception {
//            // 在这里处理拦截逻辑
//            System.out.println("Intercepted method: " + method.getName());
//
//            // 调用原始方法
//            return null;
//        }
//    }
//}



// 要代理的接口
//interface MyInterface {
//    void myMethod();
//}
