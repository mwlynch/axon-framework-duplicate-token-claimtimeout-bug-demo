# axon-framework-duplicate-token-claimtimeout-bug-demo
Demonstrates a bug resulting in multiple tracking tokens for the same processing group in axon framework 3.4.2 when
using the mongo token store.

## How to run
Launch an instance of axondb without any credentials.
I didn't ship it with the test because I'm not sure whether I'm allowed to distribute it.
Run the provided entrypoint script: ```run.sh```
Press enter when prompted for the next part of the test.


## Expectation
When the application crashes and starts up again within the token claim timeout, it should wait for the claim to expire
and then use the existing token instead of creating a second token and replaying all events.

## Reality
When the application crashes and starts up again within the token claim timeout, it creates a duplicate token for the 
processing group. This results in all the events being replayed as non-replay events. 
 
For the last event in the sequence, the handler marked with ```@AllowReplay(false)``` is invoked when it shouldn't be.

Example output (3 x graceful exits in rapid succession):

```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad26dae0de663c063e4b8', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:4}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad26dae0de663c063e4b8', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=437474}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='44588c2e-121a-4340-8e93-0978674d94dd'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='44588c2e-121a-4340-8e93-0978674d94dd'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:5}] to 127.0.0.1:27017
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
Security framework of XStream not initialized, XStream is probably vulnerable.
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: IndexTrackingToken{globalIndex=-1} for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 0
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[com.example.Main.main()] INFO org.axonframework.eventhandling.TrackingEventProcessor - Shutdown state set for Processor 'projections'. Awaiting termination...
```

```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad271ae0de6640b5f0bab', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:6}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad271ae0de6640b5f0bab', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=504665}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='8e7e63b6-4455-4a65-bc94-6e17808ae186'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='8e7e63b6-4455-4a65-bc94-6e17808ae186'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:7}] to 127.0.0.1:27017
Security framework of XStream not initialized, XStream is probably vulnerable.
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: IndexTrackingToken{globalIndex=0} for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 1
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[com.example.Main.main()] INFO org.axonframework.eventhandling.TrackingEventProcessor - Shutdown state set for Processor 'projections'. Awaiting termination...

```
```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad275ae0de66451189ed7', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:10}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad275ae0de66451189ed7', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=530683}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='f1980869-7d58-4300-8591-55b7c0d362b6'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='f1980869-7d58-4300-8591-55b7c0d362b6'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:11}] to 127.0.0.1:27017
Security framework of XStream not initialized, XStream is probably vulnerable.
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: IndexTrackingToken{globalIndex=1} for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 2
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[com.example.Main.main()] INFO org.axonframework.eventhandling.TrackingEventProcessor - Shutdown state set for Processor 'projections'. Awaiting termination...
```

At this point, please check the trackingtokens collection to see that there is only one tracking token:
http://localhost:8081/db/axonframework/trackingtokens

Example output (3 x crash exits in rapid succession):

```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad2f1ae0de665b8b748bb', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:12}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad2f1ae0de665b8b748bb', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=633534}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='b34ec78d-893d-4b5e-9698-352bf01c036e'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='b34ec78d-893d-4b5e-9698-352bf01c036e'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:13}] to 127.0.0.1:27017
Security framework of XStream not initialized, XStream is probably vulnerable.
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: IndexTrackingToken{globalIndex=2} for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 3
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
WARNING: Please consider reporting this to the maintainers of com.google.inject.internal.cglib.core.$ReflectUtils$1
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release

```
```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad2f4ae0de665fb20d6ff', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:14}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad2f4ae0de665fb20d6ff', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=585786}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='6f775978-f43f-400d-aad5-19dc5fa37e24'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='6f775978-f43f-400d-aad5-19dc5fa37e24'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:15}] to 127.0.0.1:27017
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: null for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 0
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=6f775978-f43f-400d-aad5-19dc5fa37e24, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=6f775978-f43f-400d-aad5-19dc5fa37e24, isReplay=false
WARNING: An illegal reflective access operation has occurred
WARNING: Illegal reflective access by com.google.inject.internal.cglib.core.$ReflectUtils$1 (file:/usr/share/maven/lib/guice.jar) to method java.lang.ClassLoader.defineClass(java.lang.String,byte[],int,int,java.security.ProtectionDomain)
WARNING: Please consider reporting this to the maintainers of com.google.inject.internal.cglib.core.$ReflectUtils$1
WARNING: Use --illegal-access=warn to enable warnings of further illegal reflective access operations
WARNING: All illegal access operations will be denied in a future release

```
```
[com.example.Main.main()] INFO org.mongodb.driver.cluster - Cluster created with settings {hosts=[127.0.0.1:27017], mode=SINGLE, requiredClusterType=UNKNOWN, serverSelectionTimeout='30000 ms', maxWaitQueueSize=500}
[cluster-ClusterId{value='5c4ad2f6ae0de6663dd09abd', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:1, serverValue:16}] to 127.0.0.1:27017
[cluster-ClusterId{value='5c4ad2f6ae0de6663dd09abd', description='null'}-127.0.0.1:27017] INFO org.mongodb.driver.cluster - Monitor thread successfully connected to server with description ServerDescription{address=127.0.0.1:27017, type=STANDALONE, state=CONNECTED, ok=true, version=ServerVersion{versionList=[4, 0, 0]}, minWireVersion=0, maxWireVersion=7, maxDocumentSize=16777216, roundTripTimeNanos=577719}
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.Dom4JToByteArrayConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToDom4jConverter] is ignored. It seems to rely on a class that is not available in the class loader: org/dom4j/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.XomToStringConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/Document
[com.example.Main.main()] INFO org.axonframework.serialization.ChainingConverter - ContentTypeConverter of type [class org.axonframework.serialization.xml.InputStreamToXomConverter] is ignored. It seems to rely on a class that is not available in the class loader: nu/xom/ParsingException
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled command: Command{aggregateId='cf47a190-9c49-46d8-ae1b-cf7b7adc97be'}
[com.example.Main.main()] INFO com.example.Aggregate - Aggregate handled event: Event{aggregateId='cf47a190-9c49-46d8-ae1b-cf7b7adc97be'}
[EventProcessor[projections]-0] INFO org.mongodb.driver.connection - Opened connection [connectionId{localValue:2, serverValue:17}] to 127.0.0.1:27017
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Using current Thread for last segment worker: TrackingSegmentWorker{processor=projections, segment=Segment[0/0]}
[EventProcessor[projections]-0] INFO org.axonframework.eventhandling.TrackingEventProcessor - Fetched token: null for segment: Segment[0/0]
[EventProcessor[projections]-0] INFO io.axoniq.axondb.client.axon.AxonDBEventStore - open stream: 0
[grpc-default-executor-1] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[grpc-default-executor-0] INFO io.axoniq.axondb.client.AxonDBClient - Received: mlynch-laptop:8123
[com.example.Main.main()] INFO com.example.Main - Waiting for events to be processed by projections.
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=44588c2e-121a-4340-8e93-0978674d94dd, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=8e7e63b6-4455-4a65-bc94-6e17808ae186, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=f1980869-7d58-4300-8591-55b7c0d362b6, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=b34ec78d-893d-4b5e-9698-352bf01c036e, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=6f775978-f43f-400d-aad5-19dc5fa37e24, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=6f775978-f43f-400d-aad5-19dc5fa37e24, isReplay=false
[EventProcessor[projections]-0] INFO com.example.ReplayableProjection - REPLAYABLE: id=cf47a190-9c49-46d8-ae1b-cf7b7adc97be, isReplay=false
[EventProcessor[projections]-0] INFO com.example.NonReplayableProjection - NON REPLAYABLE: id=cf47a190-9c49-46d8-ae1b-cf7b7adc97be, isReplay=false

```

Note that despite the fact that only one new event is fired per run, the last two runs contain full replays of all the 
events.

Please check the trackingtokens collection to see that there is more than one tracking token:
http://localhost:8081/db/axonframework/trackingtokens

## Likely cause
This is likely due to a bad implementation of the loadOrInsertTokenEntry method in ```MongoTokenStore```:
```java 
 private AbstractTokenEntry<?> loadOrInsertTokenEntry(String processorName, int segment) {
        Document document = mongoTemplate.trackingTokensCollection()
                                         .findOneAndUpdate(claimableTokenEntryFilter(processorName, segment),
                                                           combine(set("owner", nodeId),
                                                                   set("timestamp", clock.millis())),
                                                           new FindOneAndUpdateOptions()
                                                                   .returnDocument(ReturnDocument.AFTER));

        if (document == null) {
            try {
                AbstractTokenEntry<?> tokenEntry = new GenericTokenEntry<>(null,
                                                                           serializer,
                                                                           contentType,
                                                                           processorName,
                                                                           segment);
                tokenEntry.claim(nodeId, claimTimeout);

                mongoTemplate.trackingTokensCollection()
                             .insertOne(tokenEntryToDocument(tokenEntry));

                return tokenEntry;
            } catch (MongoWriteException exception) {
                if (ErrorCategory.fromErrorCode(exception.getError().getCode()) == ErrorCategory.DUPLICATE_KEY) {
                    throw new UnableToClaimTokenException(format("Unable to claim token '%s[%s]'",
                                                                 processorName,
                                                                 segment));
                }
            }
        }
        return documentToTokenEntry(document);
    }
```

Note that with this logic, it only selects "claimable" tokens, and then tries to claim them in a single "findOneAndUpdate" atomic operation.
So in the case where the current token is claimed, it returns a null document and then decides to create a new token.
 