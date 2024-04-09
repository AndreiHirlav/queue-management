package org.example.businessLogic;
import org.example.businessLogic.Strategy;
import org.example.model.Server;
import org.example.model.Task;

import java.util.*;
public class Scheduler {
    private List<Server> servers;
    private int noServers;
    private Strategy strategy;

    public Scheduler(int noServers, Strategy strategy) {
        this.noServers = noServers;
        this.servers = new ArrayList<>();
        this.strategy = strategy;

        //pornesc fiecare thread=coada
        for(int i = 0; i < this.noServers; i++)
        {
            Server server = new Server();
            Thread serverThread = new Thread(server);
            servers.add(server);
            serverThread.start();
        }
    }

    //adaug task-ul in serverul potrivit
    public void dispatchTask(Task t) {
        strategy.addTask(servers, t);
    }

    public List<Server> getServers() {
        return servers;
    }

}
