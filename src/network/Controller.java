package network;

import annotations.PublicAction;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.WebSocket;

import java.io.IOException;
import java.util.Map;

public abstract class Controller {

    protected Map data;
    protected WebSocket.Connection socket;

    public void init(Map data, WebSocket.Connection socket) {
        this.data = data;
        this.socket = socket;
    }

    public void response(Map data)
    {
        Gson gson = new Gson();
        try {
            socket.sendMessage(gson.toJson(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
