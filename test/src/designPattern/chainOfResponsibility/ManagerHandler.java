package com.designPattern.chainOfResponsibility;

import com.designPattern.chainOfResponsibility.Request;
import com.designPattern.chainOfResponsibility.Handler;

/**
 * ManagerHandler
 */
public class ManagerHandler extends Handler {

    private String name = "manager";

    public ManagerHandler(Handler successor) {
        super(successor);
    }

    @Override
    public void handleRequest(Request request) {
        if (request.getlevel() == 2) {
            System.out.println("\t" + name + " handler the request: \n\t\t" + request.getMsg());
            return;
        }

        if (successor != null) {
            successor.handleRequest(request);
        }
    }

}