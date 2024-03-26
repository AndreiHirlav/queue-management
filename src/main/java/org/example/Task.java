package org.example;

public class Task {
    private int arrivalTime;
    private int serviceTime;

    public Task(int arrival, int service){
        this.arrivalTime = arrival;
        this.serviceTime = service;
    }

    public int getServiceTime() {
        return serviceTime;
    }
}

