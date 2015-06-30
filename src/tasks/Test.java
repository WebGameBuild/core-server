package tasks;

import network.Producer;
import network.Task;

public class Test extends Task {
    @Override
    public void run() {
        System.out.println(vars.get("var1"));
        Producer producer = new Producer();
        producer.sendMessage();
    }
}
