package org.example.businessLogic;

import org.example.model.Server;
import org.example.model.Task;
import org.example.view.GUI;

import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class SimulationManager implements Runnable{
    private int timeLimit;
    private int maxProcessingTime;
    private int minProcessingTime;
    private int minArrivalTime;
    private int maxArrivalTime;
    private int numberOfServers;
    private int numberOfClients;
    private Strategy strategy;
    private JTextArea resultsArea;
    private Scheduler scheduler;
    private List<Task> generatedTasks = new ArrayList<>();
    private List<Task> copyClients = new ArrayList<>();
    private int peakClients;
    private int peakHour;

    public SimulationManager(int timeLimit, int maxProcessingTime, int minProcessingTime, int minArrivalTime, int maxArrivalTime, int numberOfServers, int numberOfClients, Strategy strategy, JTextArea resultsArea) {
        this.timeLimit = timeLimit;
        this.maxProcessingTime = maxProcessingTime;
        this.minProcessingTime = minProcessingTime;
        this.minArrivalTime = minArrivalTime;
        this.maxArrivalTime = maxArrivalTime;
        this.numberOfServers = numberOfServers;
        this.numberOfClients = numberOfClients;
        this.strategy = strategy;
        this.scheduler = new Scheduler(numberOfServers, strategy);
        this.resultsArea = resultsArea;
        generateNRandomTasks();
    }

    private void generateNRandomTasks() {
        for(int i = 0; i < numberOfClients; i++)
        {
            int processingTime = (int)((Math.random() * (maxProcessingTime - minProcessingTime)) + minProcessingTime);
            int arrivalTime = (int)((Math.random() * (maxArrivalTime - minArrivalTime)) + minArrivalTime);
            Task newTask = new Task(i + 1, arrivalTime, processingTime);
            generatedTasks.add(newTask);
            copyClients.add(newTask); //fac o copie cu totalul clientilor
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

            //iau urmatorul task si daca ajung la arrivalTime, il trimit la procesat + il scot din generatedTask
            while(iterator.hasNext()) {
                Task task = iterator.next();

                if(task.getArrivalTime() == currentTime) {
                    scheduler.dispatchTask(task);
                    iterator.remove();
                }
            }
            displayState(currentTime);
            if(computeNrClients() > peakClients) {
                peakClients = computeNrClients();
                peakHour = currentTime;
            }

            //daca am procesat toti clientii
            if(generatedTasks.isEmpty() && queuesEmpty()) {
                print("Average service time: " + String.format("%.2f", computeServiceAvg()) + "\n");
                print("Average waiting time: " + String.format("%.2f", computeWaitingAvg()) + "\n");
                print("Peak hour: " + peakHour + "(" + peakClients + " clients)\n");
                break;
            }

            try{
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            currentTime++;
        }
        if(currentTime == timeLimit + 1) {   //daca am ajuns la time limit
            print("Average service time: " + String.format("%.2f", computeServiceAvg()) + "(time limit exceeded)\n");
            print("Average waiting time: " + String.format("%.2f", computeWaitingAvg()) + "\n");
            print("Peak hour: " + peakHour + " (" + peakClients + " clients)\n");
        }
    }

    //construieste string-uri pentru afisare si le trimite spre afisat
    private void displayState(int currentTime) {
        print("\nTime " + currentTime + "\n");
        print("Waiting clients:\n");

        for (Task task : generatedTasks) {
            print("(" + task.getId() + ", " + task.getArrivalTime() + ", " + task.getServiceTime() + ");\n");
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
            print(queueState + "\n");
        }
    }

    //tipareste pe ecran, in fisier, in interfata
    private void print(String output) {
        String filePath = "log.txt";
        try(FileWriter writer = new FileWriter(filePath, true)) {
            System.out.print(output);
            writer.write(output);
            resultsArea.append(output);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    //verifica daca toate cozile sunt goale
    private boolean queuesEmpty() {
        for (Server server : scheduler.getServers()) {
            if (!server.getTasks().isEmpty() || server.getCurrentTask() != null) {
                return false;
            }
        }
        return true;
    }

    //media
    private double computeServiceAvg() {
        double result = 0;
        for(Server server : scheduler.getServers()) {
            result += server.getServicePeriod().get();
        }

        return result / numberOfClients;
    }

    private int computeNrClients() {
        int totalClients = 0;
        for(Server server : scheduler.getServers()) {
            totalClients += server.getNrClients();
        }

        return totalClients;
    }
    private double computeWaitingAvg() {
        double result = 0;
        for(Task client : copyClients) {
            result += client.getWaitingTime();
        }

        return result / numberOfClients;
    }

    public static void main(String[] args) {
        GUI gui= new GUI();
    }
}
