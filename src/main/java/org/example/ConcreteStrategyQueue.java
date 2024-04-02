package org.example;
import java.util.*;
import java.util.List;

public class ConcreteStrategyQueue implements Strategy {
    @Override
    public void addTask(List<Server> servers, Task t) {
        Server shortestQueue = null;
        int shortestSize = Integer.MAX_VALUE;
        for (Server server : servers) {
            int currentSize = server.getTasks().size();
            if(server.getCurrentTask() != null)
                currentSize++;
            if (currentSize < shortestSize) {
                shortestSize = currentSize;
                shortestQueue = server;
            }
        }

        if (shortestQueue != null) {
            shortestQueue.addTask(t);
        }
    }
}