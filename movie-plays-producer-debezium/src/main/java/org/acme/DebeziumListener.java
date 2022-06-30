package org.acme;

import java.io.IOException;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.context.ManagedExecutor;

import io.debezium.config.Configuration;
import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.format.Json;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;

@ApplicationScoped
public class DebeziumListener {
    
    ManagedExecutor executor;
    KafkaClientService kafkaClient;
    Configuration configuration;

    private DebeziumEngine<ChangeEvent<String, String>> engine;

    public DebeziumListener(ManagedExecutor executor, KafkaClientService kafkaClient, Configuration configuration) {
        this.executor = executor;
        this.kafkaClient = kafkaClient;
        this.configuration = configuration;
    }

    void onStart(@Observes StartupEvent event) {
        System.out.println("Initializing Debezium");
        System.out.println("*****************");
        System.out.println(this.configuration);
        System.out.println("*****************");
        this.engine = DebeziumEngine.create(Json.class)
            .using(this.configuration.asProperties())
            .notifying(this::handleChangeEvent)
            .build();

        this.executor.execute(this.engine);
    }

    void handleChangeEvent(ChangeEvent<String, String> record) {
        System.out.println("New Record");
        System.out.println(record);
    }

    void onStop(@Observes ShutdownEvent event) throws IOException {
        if (this.engine != null) {
            this.engine.close();
        }
    }

}
