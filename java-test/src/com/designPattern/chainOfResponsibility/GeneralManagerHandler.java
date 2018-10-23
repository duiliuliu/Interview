package com.designPattern.chainOfResponsibility;

import com.designPattern.chainOfResponsibility.Request;
import com.designPattern.chainOfResponsibility.Handler;

/**
 * GeneralManagerHandler
 */
public class GeneralManagerHandler extends Handler {

    private String name = "general manager";

    public GeneralManagerHandler(Handler successor) {
        super(successor);
    }

    @Override
    public void handleRequest(Request request) {
        if (request.getlevel() == 3) {
            System.out.println("\t" + name + " handler the request: \n\t\t" + request.getMsg());
            return;
        }

        if (successor != null) {
            successor.handleRequest(request);
        }
    }

}