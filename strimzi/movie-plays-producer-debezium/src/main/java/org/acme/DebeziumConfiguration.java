package org.acme;

import java.io.File;
import java.io.IOException;

import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.debezium.config.Configuration;

public class DebeziumConfiguration {
    
    // Debezium needs Database URL and credentials to login and 
    // monitor transaction logs
    @ConfigProperty(name = "quarkus.datasource.jdbc.url")
    String url;

    @ConfigProperty(name = "quarkus.datasource.password")
    String password;

    @ConfigProperty(name = "quarkus.datasource.username")
    String username;

    @Produces
    public Configuration configureDebezium() throws IOException {

        // Custom class to get database name or hostname of Database server
        MySqlJdbcParser jdbcParser = MySqlJdbcParser.parse(url);
        
        File fileOffset = File.createTempFile("offset", ".dat");
        File fileDbHistory = File.createTempFile("dbhistory", ".dat");

        return io.debezium.config.Configuration.create()
            .with("name", "movies-mysql-connector")
            // configures MySQL connector
            .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", fileOffset.getAbsolutePath())
            .with("offset.flush.interval.ms", "60000")
            // Configures database location
            .with("database.hostname", jdbcParser.getHost())
            .with("database.port", jdbcParser.getPort())
            .with("database.user", "root")
            .with("database.allowPublicKeyRetrieval", "true")
            .with("database.password", password)
            .with("database.dbname", jdbcParser.getDatabase())
            .with("database.include.list", jdbcParser.getDatabase())
            // Debezium only sends events for the modifications of OutboxEvent table and not all tables
            .with("table.include.list", jdbcParser.getDatabase().trim() + ".OutboxEvent")
            .with("include.schema.changes", "false")
            .with("database.server.id", "10181")
            .with("database.server.name", "movies-mysql-db-server")
            .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
            .with("database.history.file.filename", fileDbHistory.getAbsolutePath())
        .build();
    }

}
