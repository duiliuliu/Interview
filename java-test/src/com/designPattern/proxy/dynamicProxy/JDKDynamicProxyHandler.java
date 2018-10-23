package com.designPattern.proxy.dynamicProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.designPattern.proxy.dynamicProxy.test.UserMgr;
import com.designPattern.proxy.dynamicProxy.test.UserMgrImpl;

/**
 * JDKDynamicProxyHandler
 */
public class JDKDynamicProxyHandler implements InvocationHandler {

    private Object proxyed;

    public JDKDynamicProxyHandler(Object proxyed) {
        this.proxyed = proxyed;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("The official agency is working....");
        return method.invoke(proxyed, args);
    }

    public static void main(String[] args) {
        UserMgrImpl usermgr = new UserMgrImpl();
        // UserMgr must be interface here.
        UserMgr proxy = (UserMgr) Proxy.newProxyInstance(UserMgr.class.getClassLoader(), new Class[] { UserMgr.class },
                new JDKDynamicProxyHandler(usermgr));
        proxy.addUser();
        proxy.delUser();
    }
}