package org.example;

import java.util.List;

public class ConcreteStrategyTime implements Strategy{

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server fastestQueue = null;
        int shortestTime = Integer.MAX_VALUE;

        for(Server server : servers) {
            int currentTime = server.getWaitingPeriod().get();
            if(currentTime < shortestTime) {
                shortestTime = currentTime;
                fastestQueue = server;
            }
        }

        if(fastestQueue != null)
            fastestQueue.addTask(t);
    }


}
