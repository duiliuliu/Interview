package com.designPattern.chainOfResponsibility;

/**
 * Client
 * 
 * Connecting objects that can handle the same kind of requests into a chain
 * gives them the opportunity to process requests, and requests are passed along
 * the chain. So as to avoid the coupling relationship between the sender and
 * the receiver of the request.
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