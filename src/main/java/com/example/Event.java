package com.example;

public class Event {

    private String aggregateId;

    public Event() {
    }

    public Event(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public void setAggregateId(String aggregateId) {
        this.aggregateId = aggregateId;
    }

    @Override
    public String toString() {
        return "Event{" +
                "aggregateId='" + aggregateId + '\'' +
                '}';
    }
}
