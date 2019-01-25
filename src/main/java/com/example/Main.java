package com.example;

import com.mongodb.MongoClient;
import io.axoniq.axondb.client.AxonDBConfiguration;
import io.axoniq.axondb.client.axon.AxonDBEventStore;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.Configuration;
import org.axonframework.config.DefaultConfigurer;
import org.axonframework.config.EventHandlingConfiguration;
import org.axonframework.mongo.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.tokenstore.MongoTokenStore;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.serialization.xml.XStreamSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final String CRASH = "crash";
    private static final String GRACEFUL = "graceful";

    public static void main(String[] args) throws Exception {
        //create projections
        ReplayableProjection replayableProjection = new ReplayableProjection();
        NonReplayableProjection nonReplayableProjection = new NonReplayableProjection();

        //configure axon
        Configuration configuration = createConfiguration(Arrays.asList(replayableProjection, nonReplayableProjection));

        //start
        configuration.start();
        CommandGateway commandGateway = configuration.commandGateway();
        commandGateway.sendAndWait(new Command(UUID.randomUUID().toString()));
        while (replayableProjection.getEventsProcessed() < 1 || nonReplayableProjection.getEventsProcessed() < 1) {
            LOGGER.info("Waiting for events to be processed by projections.");
            Thread.sleep(100);
        }

        switch (args[0]) {
            case CRASH:
                System.exit(1);
                break;
            case GRACEFUL:
                configuration.shutdown();
                break;
            default:
                throw new IllegalArgumentException("Unknown mode: " + args[0]);

        }
    }


    private static Configuration createConfiguration(List<Object> projections) {
        EventHandlingConfiguration eventHandlingConfiguration = new EventHandlingConfiguration()
                .assignHandlersMatching("projections", o -> true)
                .registerTokenStore("projections", c -> new MongoTokenStore(new DefaultMongoTemplate(new MongoClient()), new XStreamSerializer()))
                .usingTrackingProcessors();
        projections.stream().forEach(o -> eventHandlingConfiguration.registerEventHandler(c -> o));

        return DefaultConfigurer.defaultConfiguration()
                .configureAggregate(Aggregate.class)
                .configureEventStore(c -> new AxonDBEventStore(
                        AxonDBConfiguration.newBuilder("localhost:8123").build(),
                        new JacksonSerializer()
                ))
                .registerModule(eventHandlingConfiguration)
                .buildConfiguration();
    }


}
