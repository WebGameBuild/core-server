package com.wg;

import datastore.DS;
import web.WebSocketServer;

public class Main {

    public static void main(String[] args) throws Exception {

        // initialize data store
        DS.morphia.mapPackage("models.db");
        DS.getDatastore().ensureIndexes();


        // initialize web socket server
        WebSocketServer wsServer = new WebSocketServer();
        wsServer.run();

    }

}
