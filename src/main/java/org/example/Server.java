package org.example;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

public class Server implements Runnable{
    private BlockingQueue<Task> tasks;
    private AtomicInteger waitingPeriod;
    private volatile Task currentTask;

    public Server() {
        this.tasks = new LinkedBlockingQueue<>();
        this.waitingPeriod = new AtomicInteger(0);
    }

    public void addTask(Task newTask){
        try {
            tasks.put(newTask);
            waitingPeriod.addAndGet(newTask.getServiceTime());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void run(){
        while(true) {
            try {
                currentTask = tasks.take();
                int remaining = currentTask.getServiceTime();
                while(remaining > 0)
                {
                    Thread.sleep(1000);
                    currentTask.setServiceTime(currentTask.getServiceTime() - 1);
                    remaining--;
                }
                currentTask = null;

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

    public void setTasks(BlockingQueue<Task> tasks) {
        this.tasks = tasks;
    }

    public AtomicInteger getWaitingPeriod() {
        return waitingPeriod;
    }

    public Task getCurrentTask() {
        return currentTask;
    }




}
