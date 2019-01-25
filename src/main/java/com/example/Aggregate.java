package com.example;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

public class Aggregate {

    private final static Logger LOGGER = LoggerFactory.getLogger(Aggregate.class);

    @AggregateIdentifier
    private String id;

    public Aggregate() {
        LOGGER.info("Aggregate no-arg constructor");
    }

    @CommandHandler
    public Aggregate(Command command) {
        LOGGER.info("Aggregate handled command: {}", command);
        apply(new Event(command.getAggregateId()));
    }


    @EventSourcingHandler
    public void on(Event event) {
        LOGGER.info("Aggregate handled event: {}", event);
        this.id = event.getAggregateId();
    }


}
