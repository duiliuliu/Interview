package com.designPattern.proxy.dynamicProxy.selfDynamicProxy;

/**
 * InvocationHandler
 */
public interface InvocationHandler {
    public void invoke(Object o, Method m);
}