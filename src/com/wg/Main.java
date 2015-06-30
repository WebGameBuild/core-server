package com.wg;

import datastore.DS;
import network.Broker;
import network.Consumer;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;

import javax.jms.*;

public class Main {

    public static void main(String[] args) throws Exception {

        Broker broker = new Broker();
        broker.run();

        Consumer consumer = new Consumer();
        consumer.isQueue = false;
        consumer.destinationName = "common";
        Thread consumerThread = new Thread(consumer);
        consumerThread.isDaemon();
        consumerThread.run();




    }

    public void initDataStorage()
    {
        DS.morphia.mapPackage("models");
    }
}
