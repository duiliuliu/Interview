package com.designPattern.chainOfResponsibility;

/**
 * Handler
 */
public abstract class Handler {

    protected Handler successor ;

    public Handler (Handler successor ){
        this.successor  = successor ;
    }
    
    public abstract void handleRequest(Request request);
}