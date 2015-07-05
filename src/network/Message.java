package network;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton on 27.06.2015.
 * Сообщение, отправляемое клиентом
 */
public class Message {
    public String controller;
    public String action;
    public HashMap<String, String> data;
}
