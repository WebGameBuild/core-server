package network;

import com.google.gson.Gson;
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

    protected final Server server = new Server(8080);
    protected final HashMap<String, UserWebSocket> connections = new HashMap<String, UserWebSocket>();

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
        public Response(String status, HashMap<String, String> data) {
            this.status = status;
            this.data = data;
        }
        public Response() {}
        public String status;
        public HashMap<String, String> data;
    }

    class UserWebSocket implements WebSocket.OnTextMessage {

        public Connection connection;

        @Override
        public void onMessage(String data) {
            Gson gson = new Gson();
            Message msg = gson.fromJson(data.trim(), Message.class);
            try {
                if (msg.action == null) {
                    throw new IllegalAccessException("Action not specified");
                }
                if (msg.action.matches(".*[^a-zA-Z].*")) {
                    throw new IllegalAccessException("Wrong class name");
                }
                Controller controller = (Controller) Class.forName("controllers." + msg.controller).newInstance();
                controller.init(msg.data, connection);
                Method action = controller.getClass().getMethod(msg.action, HashMap.class);
                Response response = new Response();
                response.data = (HashMap<String, String>) action.invoke(controller, msg.data);
                response.status = "success";
                this.connection.sendMessage(gson.toJson(response));
            } catch (NoSuchMethodException e) {
                System.out.println("Action not found: " + msg.action);
            } catch (NoClassDefFoundError e) {
                System.out.println("Controller not found: " + msg.action);
            } catch (ClassNotFoundException e) {
                System.out.println("Controller not found: " + msg.action);
            } catch (IllegalAccessException e) {
                System.out.println("Action wrong name:" + msg.action);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onOpen(Connection connection) {
            this.connection = connection;
            System.out.println("Client connected: " + connection.toString());
            connections.put(connection.toString(), this);
        }

        @Override
        public void onClose(int closeCode, String message) {
            System.out.println("Client disconnected: " + connection.toString());
            connections.remove(connection.toString());
            connection.close();
        }
    }

}
