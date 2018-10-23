package com.designPattern.chainOfResponsibility;

import com.designPattern.chainOfResponsibility.Request;
import com.designPattern.chainOfResponsibility.Handler;

/**
 * DirectorHandler
 */
public class DirectorHandler extends Handler {

    private String name = "director";

    public DirectorHandler(Handler successor) {
        super(successor);
    }

    @Override
    public void handleRequest(Request request) {
        if (request.getlevel() == 1) {
            System.out.println("\t" + name + " handler the request: \n\t\t" + request.getMsg());
            return;
        }

        if (successor != null) {
            successor.handleRequest(request);
        }
    }
}