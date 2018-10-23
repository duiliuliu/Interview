package com.designPattern.proxy.dynamicProxy.test;

/**
 * userMgrImpl
 */
public class UserMgrImpl implements UserMgr {

    @Override
    public void addUser() {
        System.out.println("add user.....");
    }

    @Override
    public void delUser() {
        System.out.println("del user.....");
    }
}