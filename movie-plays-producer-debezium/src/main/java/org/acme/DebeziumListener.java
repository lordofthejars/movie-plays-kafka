package org.acme;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.eclipse.microprofile.context.ManagedExecutor;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.config.Configuration;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.Record;

import static io.debezium.data.Envelope.FieldName.*;
import static io.debezium.data.Envelope.Operation;

@ApplicationScoped
public class DebeziumListener {
    
    private static ObjectMapper objectMapper = new ObjectMapper();

    // Start Debezium engine in different thread
    ManagedExecutor executor;

    // Debezium configuration object
    Configuration configuration;

    private DebeziumEngine<RecordChangeEvent<SourceRecord>> engine;

    public DebeziumListener(ManagedExecutor executor, Configuration configuration) {
        this.executor = executor;
        this.configuration = configuration;
    }

    // Interface to send events to movies Kafka topic
    @Channel("movies")
    Emitter<Record<Long, JsonNode>> movieEmitter;

    void onStart(@Observes StartupEvent event) {

        // Configures Debezium engine
        this.engine = DebeziumEngine.create(ChangeEventFormat.of(Connect.class))
            .using(this.configuration.asProperties())
            // For each event triggered by Debezium, the handleChangeEvnt method is called
            .notifying(this::handleChangeEvent)
            .build();

        // Starts Debezium in different thread
        this.executor.execute(this.engine);
    }

    void handleChangeEvent(RecordChangeEvent<SourceRecord> sourceRecordRecordChangeEvent) {

        // For each triggered event, we get the information
        SourceRecord sourceRecord = sourceRecordRecordChangeEvent.record();
        Struct sourceRecordChangeValue= (Struct) sourceRecord.value();

        if (sourceRecordChangeValue != null) {
            Operation operation = Operation.forCode((String) sourceRecordChangeValue.get(OPERATION));

            // Only insert operations are processed
            if(operation == Operation.CREATE) {

                // Get insertation info
                Struct struct = (Struct) sourceRecordChangeValue.get(AFTER);
                String type = struct.getString("type");
                String payload = struct.getString("payload");

                if ("MovieCreated".equals(type)) {
                    try {
                        final JsonNode payloadJson = objectMapper.readValue(payload, JsonNode.class);
                        long id = payloadJson.get("id").asLong();

                        // Populate content to Kafka topic
                        movieEmitter.send(Record.of(id, payloadJson));
                    } catch (JsonProcessingException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            }
        }
    }

    void onStop(@Observes ShutdownEvent event) throws IOException {
        if (this.engine != null) {
            this.engine.close();
        }
    }

}
