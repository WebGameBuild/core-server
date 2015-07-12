package web;

import java.util.HashMap;

public class JsonData extends HashMap<String, Object> {
    public JsonData(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public JsonData(int initialCapacity) {
        super(initialCapacity);
    }
    public JsonData() {
        super();
    }
}