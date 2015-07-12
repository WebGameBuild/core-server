package controllers;

import web.annotations.PrivateAction;
import web.annotations.PublicAction;
import web.Controller;
import web.JsonData;
import web.WebSocketServer;

public class TestController extends Controller {

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
