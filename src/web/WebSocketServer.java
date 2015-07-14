package web;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import models.db.User;
import org.bson.types.ObjectId;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.ExecutorThreadPool;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;
import web.annotations.UserAction;
import web.annotations.PublicAction;
import web.exceptions.InvalidRequestException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;


public class WebSocketServer implements Runnable {

    protected final Server server = new Server(82);
    public static final HashMap<ObjectId, UserWebSocket> connections = new HashMap<ObjectId, UserWebSocket>();

    public void run() {

        try {
            ContextHandler contextHandler = new ContextHandler();
            contextHandler.setHandler(new Handler());
            contextHandler.setContextPath("/");
            server.setHandler(contextHandler);

            //thread pool settings
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>(100);
            ExecutorThreadPool pool = new ExecutorThreadPool(5, 200, 0, TimeUnit.MILLISECONDS, queue);
            server.setThreadPool(pool);

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
        public String status;
        public JsonData data;
        public HashMap<String, Object> request = new HashMap<>();
    }

    public class UserWebSocket implements WebSocket.OnTextMessage {

        public Connection connection;
        public User user;

        @Override
        public void onMessage(String data) {
            Gson gson = new Gson();
            Response response = new Response();
            try {
                Message msg = gson.fromJson(data.trim(), Message.class);
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
            if (user != null) {
                connections.remove(user.id);
            }
            connection.close();
        }
    }

}
