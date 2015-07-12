package controllers;

import datastore.DS;
import models.db.Session;
import models.db.User;
import web.Controller;
import web.JsonData;
import web.WebSocketServer;
import web.annotations.PublicAction;
import web.annotations.Validator;
import web.exceptions.InvalidRequestException;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.UUID;

public class Auth extends Controller {

    @PublicAction
    @Validator(param = "username", type = String.class, required = true)
    @Validator(param = "password", type = String.class, required = true)
    public JsonData register(JsonData request, WebSocketServer.UserWebSocket socket)
            throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidRequestException {
        String username = request.get("username").toString();
        if (DS.getDatastore().createQuery(User.class).filter("username", username).countAll() > 0) {
            throw new InvalidRequestException("Username already in use");
        }
        JsonData response = new JsonData();
        User user = new User();
        user.username = username;
        user.password_hash = sha256(request.get("password").toString());
        user.created_at = new Date();
        user.updated_at = new Date();

        DS.getDatastore().save(user);

        return response;
    }

    @PublicAction
    @Validator(param = "username", type = String.class, required = true)
    @Validator(param = "password", type = String.class, required = true)
    public JsonData getToken(JsonData request, WebSocketServer.UserWebSocket socket)
            throws InvalidRequestException {
        JsonData response = new JsonData();
        User user = DS.getDatastore().createQuery(User.class)
                .filter("username", request.get("username").toString())
                .filter("password_hash", sha256(request.get("password").toString()))
                .get();

        if (user == null) {
            throw new InvalidRequestException("Invalid login/password pair");
        }

        Session session = new Session();
        session.user_id = user.id;
        session.token = UUID.randomUUID().toString();
        DS.getDatastore().save(session);

        response.put("token", session.token);

        return response;
    }

    @PublicAction
    @Validator(param = "token", required = true)
    public JsonData assignToken(JsonData request, WebSocketServer.UserWebSocket socket)
            throws InvalidRequestException {
        JsonData response = new JsonData();
        Session session = DS.getDatastore().createQuery(Session.class)
                .filter("token", request.get("token").toString())
                .get();

        if (session == null) {
            throw new InvalidRequestException("Invalid token");
        }

        socket.user = DS.getDatastore().get(User.class, session.user_id);
        socket.user.touch();

        WebSocketServer.connections.put(session.user_id, socket);

        return response;
    }

    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();

            for (byte aHash : hash) {
                String hex = Integer.toHexString(0xff & aHash);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
