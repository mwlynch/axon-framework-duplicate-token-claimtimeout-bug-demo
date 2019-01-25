package com.example;

import org.axonframework.commandhandling.TargetAggregateIdentifier;

public class Command {

    @TargetAggregateIdentifier
    private String aggregateId;

    public Command() {
    }

    public Command(String aggregateId) {
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
        return "Command{" +
                "aggregateId='" + aggregateId + '\'' +
                '}';
    }
}
