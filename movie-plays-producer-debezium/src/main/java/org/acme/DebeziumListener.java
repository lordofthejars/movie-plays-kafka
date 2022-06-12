package org.acme;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.kafka.clients.KafkaClient;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.context.ManagedExecutor;

import io.debezium.config.Configuration;
import io.debezium.embedded.EmbeddedEngine;
import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.reactive.messaging.kafka.KafkaClientService;

@ApplicationScoped
public class DebeziumListener {
    
    ManagedExecutor executor;

    KafkaClientService kafkaClient;

    Configuration configuration;

    public DebeziumListener(ManagedExecutor executor, KafkaClientService kafkaClient, Configuration configuration) {
        this.executor = executor;
        this.kafkaClient = kafkaClient;
        this.configuration = configuration;
    }

    EmbeddedEngine engine;

    void onStart(@Observes StartupEvent event) {
        System.out.println(kafkaClient);
        System.out.println(executor);
        System.out.println(configuration);
    }

    void onStop(@Observes ShutdownEvent event) {
        if (this.engine != null) {
            this.engine.stop();
        }
    }

}
