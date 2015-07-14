package web;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import models.db.User;
import org.bson.types.ObjectId;
import org.eclipse.jetty.io.Connection;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import web.annotations.UserAction;
import web.annotations.PublicAction;
import web.exceptions.InvalidRequestException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class WebSocketServer implements Runnable {

    protected final Server server = new Server(82);
    public static final HashMap<ObjectId, UserWebSocket> connections = new HashMap<ObjectId, UserWebSocket>();

    public void run() {

        try {
            ServletContextHandler contextHandler = new ServletContextHandler();
            contextHandler.setContextPath("/");
            contextHandler.addServlet(StockServiceSocketServlet.class, "/");
            server.setHandler(contextHandler);
            server.start();
            server.join();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static class StockServiceSocketServlet extends WebSocketServlet {
        @Override
        public void configure(WebSocketServletFactory factory) {
            factory.getPolicy().setIdleTimeout(0);
            factory.register(UserWebSocket.class);
        }
    }

    public static class Response {
        public String status;
        public JsonData data;
        public HashMap<String, Object> request = new HashMap<>();
    }

    @WebSocket
    public static class UserWebSocket {

        private Session session;
        public User user;

        @OnWebSocketMessage
        public void onMessage(String message) {
            Gson gson = new Gson();
            Response response = new Response();
            try {
                Message msg = gson.fromJson(message.trim(), Message.class);
                response.request.put("controller", msg.controller);
                response.request.put("action", msg.action);
                response.request.put("id", msg.id);
                try {
                    try {
                        if (msg.action == null) {
                            throw new IllegalAccessException("Action not specified");
                        }
                        if (msg.action.matches(".*[^a-zA-Z].*")) {
                            throw new IllegalAccessException("Illegal action name");
                        }
                        Controller controller = (Controller) Class
                                .forName("controllers." + msg.controller + "Controller")
                                .newInstance();
                        Method action = controller.getClass().getMethod(msg.action, JsonData.class, UserWebSocket.class);
                        if (action.isAnnotationPresent(PublicAction.class)
                                || (action.isAnnotationPresent(UserAction.class) && this.user != null)) {
                            // выкинет исключение если не пройдет валидация
                            controller.validate(action, msg.data);
                            response.data = (JsonData) action.invoke(controller, msg.data, this);
                        } else {
                            throw new InvalidRequestException("Access forbidden for anonymous user: " + msg.controller + "/" + msg.action);
                        }
                        response.status = "success";
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        throw new InvalidRequestException("Invalid action: " + msg.action);
                    } catch (NoClassDefFoundError | ClassNotFoundException e) {
                        throw new InvalidRequestException("Controller not found: " + msg.controller);
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

            } catch (JsonSyntaxException e) {
                response.status = "error";
                response.data = new JsonData(1);
                response.data.put("message", e.getMessage());
            }

            send(gson.toJson(response));
        }

        // called in case of an error
        @OnWebSocketError
        public void onError(Throwable error) {
            error.printStackTrace();
        }

        @OnWebSocketConnect
        public void onConnect(Session session) {
            this.session = session;
            System.out.println("Client connected: " + session.toString());
        }

        @OnWebSocketClose
        public void onClose(int statusCode, String reason) {
            System.out.println("Client disconnected: " + session.toString());
            if (user != null) {
                connections.remove(user.id);
            }
        }

        // sends message to browser
        private void send(String message) {
            try {
                if (session.isOpen()) {
                    session.getRemote().sendString(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // closes the socket
        private void stop() {
            try {
                session.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
