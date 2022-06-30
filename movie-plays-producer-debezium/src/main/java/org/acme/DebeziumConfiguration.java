package org.acme;

import java.net.URI;
import java.util.Properties;

import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.debezium.config.Configuration;

public class DebeziumConfiguration {
    
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String url;

    @ConfigProperty(name = "quarkus.datasource.password")
    String password;

    @ConfigProperty(name = "quarkus.datasource.username")
    String username;

    @Produces
    public Configuration configureDebezium() {

        MySqlJdbcParser jdbcParser = MySqlJdbcParser.parse(url);

        return io.debezium.config.Configuration.create()
            .with("name", "movies-mysql-connector")
            .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", "/tmp/offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            .with("database.hostname", jdbcParser.getHost())
            .with("database.port", jdbcParser.getPort())
            .with("database.user", "root")
            .with("database.allowPublicKeyRetrieval", "true")
            .with("database.password", password)
            .with("database.dbname", jdbcParser.getDatabase())
            .with("database.include.list", jdbcParser.getDatabase())
            //.with("schema.include.list", "public")
            .with("table.include.list", "public.OutboxEvent")
            .with("include.schema.changes", "false")
            .with("database.server.id", "10181")
            .with("database.server.name", "movies-mysql-db-server")
            .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
            .with("database.history.file.filename", "/tmp/dbhistory.dat")
        .build();
    }

}
