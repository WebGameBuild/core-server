package network;

import annotations.PrivateAction;
import annotations.PublicAction;
import com.google.gson.Gson;
import models.db.User;
import network.exceptions.InvalidRequestException;
import org.bson.types.ObjectId;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;


public class WebSocketServer implements Runnable {

    protected final Server server = new Server(82);
    public static final HashMap<ObjectId, UserWebSocket> connections = new HashMap<ObjectId, UserWebSocket>();

    public void run() {

        try {
            ContextHandler contextHandler = new ContextHandler();
            contextHandler.setHandler(new Handler());
            contextHandler.setContextPath("/");
            server.setHandler(contextHandler);
            server.start();
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    class Handler extends WebSocketHandler {

        @Override
        public WebSocket doWebSocketConnect(HttpServletRequest request, String protocol) {
            return new UserWebSocket();
        }
    }

    class Response {
        public Response(String status, JsonData data) {
            this.status = status;
            this.data = data;
        }

        public Response() {
        }

        public String status;
        public JsonData data;
    }

    public class UserWebSocket implements WebSocket.OnTextMessage {

        public Connection connection;
        public User user;

        @Override
        public void onMessage(String data) {
            Gson gson = new Gson();
            Message msg = gson.fromJson(data.trim(), Message.class);
            Response response = new Response();
            try {
                try {
                    if (msg.action == null) {
                        throw new IllegalAccessException("Action not specified");
                    }
                    if (msg.action.matches(".*[^a-zA-Z].*")) {
                        throw new IllegalAccessException("Wrong class name");
                    }
                    Controller controller = (Controller) Class.forName("controllers." + msg.controller).newInstance();
                    Method action = controller.getClass().getMethod(msg.action, JsonData.class, UserWebSocket.class);
                    if (action.isAnnotationPresent(PublicAction.class)
                            || action.isAnnotationPresent(PrivateAction.class)) {
                        response.data = (JsonData) action.invoke(controller, msg.data, this);
                    } else {
                        throw new InvalidRequestException("Access forbidden: " + msg.controller + "/" + msg.action);
                    }
                    response.status = "success";
                } catch (NoSuchMethodException e) {
                    throw new InvalidRequestException("Invalid action: " + msg.action);
                } catch (NoClassDefFoundError e) {
                    throw new InvalidRequestException("Controller not found: " + msg.controller);
                } catch (ClassNotFoundException e) {
                    throw new InvalidRequestException("Controller not found: " + msg.controller);
                } catch (IllegalAccessException e) {
                    throw new InvalidRequestException("Invalid action: " + msg.action);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    Throwable targetException = e.getTargetException();
                    if (targetException instanceof InvalidRequestException) {
                        throw (InvalidRequestException) targetException;
                    } else {
                        e.printStackTrace();
                    }
                }
            } catch (InvalidRequestException requestException) {
                response.status = "error";
                response.data = new JsonData(1);
                response.data.put("message", requestException.getMessage());
            }

            try {
                this.connection.sendMessage(gson.toJson(response));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onOpen(Connection connection) {
            this.connection = connection;
            connection.setMaxIdleTime(0);
            System.out.println("Client connected: " + connection.toString());
        }

        @Override
        public void onClose(int closeCode, String message) {
            System.out.println("Client disconnected: " + connection.toString());
            if(user != null) {
                connections.remove(user.id);
            }
            connection.close();
        }
    }

}
