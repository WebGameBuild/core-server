package controllers;

import annotations.PrivateAction;
import annotations.PublicAction;
import network.Controller;
import network.JsonData;

public class Test extends Controller {

    @PrivateAction
    public JsonData echo(JsonData params) {
        JsonData data = new JsonData();
        data.put("loh", params.get("text"));
        return data;
    }

    @PublicAction
    public JsonData build(JsonData params) {
        JsonData data = new JsonData();
        data.put("loh", params.get("text"));
        return data;
    }
}
