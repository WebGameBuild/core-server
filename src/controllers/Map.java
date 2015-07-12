package controllers;

import web.Controller;
import web.JsonData;

import java.util.ArrayList;
import java.util.HashMap;

public class Map extends Controller {

    public JsonData get(JsonData request) {
        class Cell extends HashMap<String, Integer> {

        }
        JsonData response = new JsonData();
        ArrayList cells = new ArrayList<Cell>();


        return  response;
    }

}
