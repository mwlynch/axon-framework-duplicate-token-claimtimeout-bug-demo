package com.example;

import org.axonframework.eventhandling.AllowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventhandling.ReplayStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NonReplayableProjection {

    private final static Logger LOGGER = LoggerFactory.getLogger(NonReplayableProjection.class);
    private int eventsProcessed = 0;

    @EventHandler
    @AllowReplay(false)
    public void on(Event aggregateCreatedEvent, ReplayStatus replayStatus) {
        LOGGER.info("NON REPLAYABLE: id={}, isReplay={}", aggregateCreatedEvent.getAggregateId(), replayStatus.isReplay());
        ++eventsProcessed;
    }

    public int getEventsProcessed() {
        return eventsProcessed;
    }
}
