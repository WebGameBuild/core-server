package controllers;

import org.mongodb.morphia.query.Query;
import web.Controller;
import web.JsonData;
import web.annotations.PublicAction;
import web.annotations.Validator;

import java.util.ArrayList;
import java.util.HashMap;

public class LandController extends Controller {

    @PublicAction
    @Validator(param = "leftTop", type = Integer.class, required = true)
    @Validator(param = "rightTop", type = Integer.class, required = true)
    @Validator(param = "rightBottom", type = Integer.class, required = true)
    @Validator(param = "leftBottom", type = Integer.class, required = true)
    public JsonData get(JsonData request) {
        class Cell extends HashMap<String, Integer> {}
        JsonData response = new JsonData();
        ArrayList cells = new ArrayList<Cell>();

//        models.db.Land[] lands

        return  response;
    }

}
