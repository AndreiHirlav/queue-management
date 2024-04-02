package org.example;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable{
    public int timeLimit = 100;
    public int maxProcessingTime = 10;
    public int minProcessingTime = 2;
    public int minArrivalTime = 2;
    public int maxArrivalTime = 30;
    public int numberOfServers = 3;
    public int numberOfClients = 3;
    public Strategy strategy = new ConcreteStrategyTime();

    private Scheduler scheduler;
    private List<Task> generatedTasks = new ArrayList<>();

    public SimulationManager() {
        this.scheduler = new Scheduler(numberOfServers, strategy);
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        for(int i = 0; i < numberOfClients; i++)
        {
            int processingTime = (int)((Math.random() * (maxProcessingTime - minProcessingTime)) + minProcessingTime);
            int arrivalTime = (int)((Math.random() * (maxArrivalTime - minArrivalTime)) + minArrivalTime);
            generatedTasks.add(new Task(i + 1, arrivalTime, processingTime));
        }

        Collections.sort(generatedTasks, new Comparator<Task>() {
            @Override
            public int compare(Task o1, Task o2) {
                return Integer.compare(o1.getArrivalTime(), o2.getArrivalTime());
            }
        });
    }

    @Override
    public void run() {
        int currentTime = 0;
        while(currentTime <= timeLimit)
        {
            Iterator<Task> iterator = generatedTasks.iterator();
            while(iterator.hasNext()) {
                Task task = iterator.next();

                if(task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    iterator.remove();
                }
            }

            if(generatedTasks.isEmpty() && queuesEmpty()) {
                System.out.println("Average waiting time: " + String.format("%.2f", computeWaitingAvg()));
                break;
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            displayState(currentTime);
            currentTime++;
        }
    }

    private void displayState(int currentTime) {
        String filePath = "log.txt";

        try(FileWriter writer = new FileWriter(filePath, true)) {
        System.out.println("\nTime " + currentTime);
        writer.write("\nTime " + currentTime + "\n"); //file
        System.out.println("Waiting clients:");
        writer.write("Waiting clients:\n");
        for (Task task : generatedTasks) {
            System.out.println("(" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getServiceTime() + ");");
            writer.write("(" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getServiceTime() + ");\n");
        }

        //tiparesc fiecare server
        for (int i = 0; i < numberOfServers; i++) {
            Server server = scheduler.getServers().get(i);
            String queueState = "Queue " + (i + 1) + ": ";
            Task currentTask = server.getCurrentTask();

            //si task-ul curent
            if (currentTask != null) {
                queueState += "(" + currentTask.getId() + "," + currentTask.getArrivalTime() + "," + currentTask.getServiceTime() + "); ";
            }

            //Dupa tiparesc restul task-urilor din server
            for (Task task : server.getTasks()) {
                if (task != currentTask) { //in afara de task-ul curent
                    queueState += "(" + task.getId() + "," + task.getArrivalTime() + "," + task.getServiceTime() + "); ";
                }
            }

            if (queueState.equals("Queue " + (i + 1) + ": ")) {//Ca sa tipareasca closed
                queueState += "closed";
            }
            writer.append(queueState + "\n");
            System.out.println(queueState);
        }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private boolean queuesEmpty() {
        for (Server server : scheduler.getServers()) {
            if (!server.getTasks().isEmpty() || server.getCurrentTask() != null) {
                return false;
            }
        }
        return true;
    }

    private double computeWaitingAvg() {
        double result = 0;
        for(Server server : scheduler.getServers()) {
            result += server.getWaitingPeriod().get();
        }

        return result / numberOfClients;
    }
    public static void main(String[] args) {
        SimulationManager simulationManager = new SimulationManager();
        Thread simulationThread = new Thread(simulationManager);

        simulationThread.start();
    }
}
