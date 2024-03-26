package org.example;
import java.util.*;
public class Scheduler {
    private List<Server> servers;
    private int noServers;
    private int taskPerServer;
    private Strategy strategy;

    public Scheduler(int noServers, int tasksServer) {
        this.noServers = noServers;
        this.taskPerServer = tasksServer;
        this.servers = new ArrayList<>();

        for(int i = 0; i < this.noServers; i++)
        {
            Server server = new Server();
            Thread serverThread = new Thread(server);
            serverThread.start();
        }
    }



}
