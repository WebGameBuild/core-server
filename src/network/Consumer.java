package network;

import com.google.gson.Gson;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQBytesMessage;
import javax.jms.*;
import javax.jms.IllegalStateException;


public class Consumer implements Runnable {
    public Boolean isQueue = false;
    public String destinationName;
    public Task handler;

    @Override
    public void run() {
        try {
            // Create a ConnectionFactory
            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("vm://localhost:61613");
            // Create a Connection
            Connection connection = null;
            connection = connectionFactory.createConnection();
            connection.start();

            // Create a Session
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination;
            // Create the destination (Topic or Queue)
            if (isQueue) {
                destination = session.createQueue(destinationName);
            } else {
                destination = session.createTopic(destinationName);
            }
            // Create a MessageConsumer from the Session to the Topic or Queue
            MessageConsumer consumer = session.createConsumer(destination);

            while (!Thread.currentThread().isInterrupted()) {
                // Wait for a message
                ActiveMQBytesMessage message = (ActiveMQBytesMessage) consumer.receive(0);
                String data = new String(message.getContent().getData());
                Gson gson = new Gson();
                Message msg = gson.fromJson(data.trim(), Message.class);
                try {
                    if(msg.action.matches(".*[^a-zA-Z].*")) {
                        throw new IllegalAccessException("Wrong class name");
                    }
                    Task task = (Task) Class.forName("tasks." + msg.action).newInstance();
                    task.vars = msg.vars;
                    task.run();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException  e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
            }

            consumer.close();
            session.close();
            connection.close();

        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void stop() {

    }
}
