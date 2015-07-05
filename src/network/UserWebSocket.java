package network;

import org.eclipse.jetty.websocket.WebSocket;

/**
 * Created by Anton on 30.06.2015.
 */
public class UserWebSocket implements WebSocket.OnTextMessage {

    @Override
    public void onMessage(String data) {
        System.out.println(data);
    }

    @Override
    public void onOpen(Connection connection) {
        System.out.println(connection);

//            connections.put("key", this);
    }

    @Override
    public void onClose(int closeCode, String message) {
        System.out.println(message);
//            connections.remove("key");
    }
}

