package network;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import java.util.Map;

/**
 * Created by Anton on 27.06.2015.
 * Сообщение, отправляемое клиентом через Stomp
 */
public class Message {
    public String action;
    public Map vars;
}
