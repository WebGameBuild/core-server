package controllers;

import network.Controller;

import java.util.HashMap;

public class Test extends Controller {

    public HashMap build(HashMap<String, String> params) {
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("loh", params.get("text"));
        return data;
    }
}
