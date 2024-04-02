package org.example;

public class Task {
    private int arrivalTime;
    private int serviceTime;
    private int id;

    public Task(int id, int arrival, int service){
        this.id = id;
        this.arrivalTime = arrival;
        this.serviceTime = service;
    }

    public int getServiceTime() {
        return serviceTime;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getId() {
        return this.id;
    }

    public void setServiceTime(int serviceTime) {
        this.serviceTime = serviceTime;
    }
}

