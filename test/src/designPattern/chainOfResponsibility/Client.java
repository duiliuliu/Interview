package com.designPattern.chainOfResponsibility;

/**
 * Client
 */
public class Client {

    public static void main(String[] args) {
        Handler generalManagerHandler = new GeneralManagerHandler(null);
        Handler managerHandler = new ManagerHandler(generalManagerHandler);
        Handler directorHandler = new DirectorHandler(managerHandler);

        Request request1 = new Request(1,
                "the request's level is 1 and request body is the person need one day to rest \n");
        directorHandler.handleRequest(request1);

        Request request2 = new Request(2,
                "the request's level is 2 and request body is the person need ten day to rest \n");
        directorHandler.handleRequest(request2);
    }
}