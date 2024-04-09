package org.example.businessLogic;

import org.example.businessLogic.Strategy;
import org.example.model.Server;
import org.example.model.Task;

import java.util.List;

public class ConcreteStrategyTime implements Strategy {

    @Override
    public void addTask(List<Server> servers, Task t) {
        Server fastestQueue = null;
        int shortestTime = Integer.MAX_VALUE;

        //iau coada cu service time cel mai mic, iau in considerare si current task
        for(Server server : servers) {
            int currentTime = server.getServicePeriod().get();
            if(server.getCurrentTask() != null) {
                currentTime += server.getCurrentTask().getServiceTime();
            }
            if(currentTime < shortestTime) {
                shortestTime = currentTime;
                fastestQueue = server;
            }
        }

        if(fastestQueue != null)
            fastestQueue.addTask(t);
    }


}
