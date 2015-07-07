package controllers;

import network.Controller;
import network.JsonData;

public class Test extends Controller {

    public JsonData build(JsonData params) {
        JsonData data = new JsonData();
        data.put("loh", params.get("text"));
        return data;
    }
}
