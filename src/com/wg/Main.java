package com.wg;

import datastore.DS;
import network.WebSocketServer;

public class Main {

    public static void main(String[] args) throws Exception {

        WebSocketServer wsServer = new WebSocketServer();
        wsServer.run();

    }

    public void initDataStorage()
    {
        DS.morphia.mapPackage("models");
    }
}
