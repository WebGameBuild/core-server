package controllers;

import annotations.PrivateAction;
import annotations.PublicAction;
import network.Controller;
import network.JsonData;
import network.WebSocketServer;

public class Test extends Controller {

    @PrivateAction
    public JsonData echo(JsonData request, WebSocketServer.UserWebSocket socket) {
        JsonData data = new JsonData();
        data.put("loh", request.get("text"));
        return data;
    }

    @PublicAction
    public JsonData myAccount(JsonData request, WebSocketServer.UserWebSocket socket) {
        JsonData data = new JsonData();
        data.put("username", socket.user.username);

        return data;
    }
}
