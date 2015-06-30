package network;

import org.apache.activemq.broker.BrokerService;

import java.beans.ExceptionListener;

/**
 * Created by Anton on 27.06.2015.
 */
public class Broker implements Runnable {

    @Override
    public void run() {
        BrokerService brokerService = new BrokerService();
        brokerService.setPersistent(false);
        brokerService.setAdvisorySupport(false);

        try {
            brokerService.addConnector("stomp://localhost:61613?trace=true");
            brokerService.addConnector("ws://localhost:61614?trace=true");
            brokerService.start();
            brokerService.waitUntilStarted();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
