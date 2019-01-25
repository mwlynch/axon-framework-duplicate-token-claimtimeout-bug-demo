package com.example;

import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ReplayStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplayableProjection {
    private final static Logger LOGGER = LoggerFactory.getLogger(ReplayableProjection.class);
    private int eventsProcessed = 0;

    @EventHandler
    public void on(Event aggregateCreatedEvent, ReplayStatus replayStatus) {
        LOGGER.info("REPLAYABLE: id={}, isReplay={}", aggregateCreatedEvent.getAggregateId(), replayStatus.isReplay());
        ++eventsProcessed;
    }

    public int getEventsProcessed() {
        return eventsProcessed;
    }

}
