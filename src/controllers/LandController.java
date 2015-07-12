package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import datastore.DS;
import models.db.Land;
import org.mongodb.morphia.query.Query;
import web.Controller;
import web.JsonData;
import web.WebSocketServer;
import web.annotations.PublicAction;
import web.annotations.Validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class LandController extends Controller {

    @PublicAction
    @Validator(param = "x1", type = Double.class, required = true)
    @Validator(param = "y1", type = Double.class, required = true)
    @Validator(param = "x2", type = Double.class, required = true)
    @Validator(param = "y2", type = Double.class, required = true)
    public JsonData square(JsonData request, WebSocketServer.UserWebSocket socket) {
        JsonData response = new JsonData();

        Query<Land> query = DS.getDatastore().createQuery(Land.class);
        query.field("x").greaterThanOrEq(((Double) request.get("x1")).intValue());
        query.field("y").greaterThanOrEq(((Double) request.get("y1")).intValue());
        query.field("x").lessThanOrEq(((Double) request.get("x2")).intValue());
        query.field("y").lessThanOrEq(((Double) request.get("y2")).intValue());

        ArrayList<HashMap<String, Integer>> cells = new ArrayList<>();
        for (Land land : query.asList()) {
            HashMap<String, Integer> cell = new HashMap<>();
            cell.put("x", land.x);
            cell.put("y", land.y);
            cells.add(cell);
        }

        response.put("cells", cells);
        return  response;
    }

}
