package org.example.model;
import org.example.model.Task;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger servicePeriod;
    private volatile Task currentTask;
    private int nrClients;  //as fi putut sa iau tasks + 1, dar e mai lizibil asa
    private AtomicInteger totalWaiting;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.servicePeriod = new AtomicInteger(0);
        this.totalWaiting = new AtomicInteger(0);
    }

    public void addTask(Task newTask){
        try {
            tasks.put(newTask);
            servicePeriod.addAndGet(newTask.getServiceTime());
            newTask.setWaitingTime(totalWaiting.get());   //cand adaug clientul in lista ii dau si waiting time-ul curent
            totalWaiting.addAndGet(newTask.getServiceTime());  //pe masura ce adaug clienti creste si waiting time
            nrClients++;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run(){
        while(true) {
            try {
                //iau primul proces din coada
                currentTask = tasks.take();
                int remaining = currentTask.getServiceTime();
                while(remaining > 0)
                {
                    Thread.sleep(1000);
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    totalWaiting.addAndGet(-1);   //si scade si waiting time
                    remaining--;
                }
                currentTask = null;
                nrClients--;

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Coada se inchide");
                break;
            }

        }
    }

    public BlockingQueue<Task> getTasks() {
        return tasks;
    }

    public AtomicInteger getServicePeriod() {
        return servicePeriod;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public int getNrClients() {
        return nrClients;
    }
}
