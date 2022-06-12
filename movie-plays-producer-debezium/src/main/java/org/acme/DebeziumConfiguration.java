package org.acme;

import java.net.URI;
import java.util.Properties;

import javax.enterprise.inject.Produces;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.postgresql.PGProperty;

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

        Properties props = org.postgresql.Driver.parseURL(url, null);

        return io.debezium.config.Configuration.create()
            .with("name", "customer-mysql-connector")
            .with("connector.class", "io.debezium.connector.mysql.MySqlConnector")
            .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
            .with("offset.storage.file.filename", "/tmp/offsets.dat")
            .with("offset.flush.interval.ms", "60000")
            .with("database.hostname", props.getProperty(PGProperty.PG_HOST.getName()))
            .with("database.port", props.getProperty(PGProperty.PG_PORT.getName()))
            .with("database.user", username)
            .with("database.password", password)
            .with("database.dbname", props.getProperty(PGProperty.PG_DBNAME.getName()))
            .with("database.include.list", props.getProperty(PGProperty.PG_DBNAME.getName()))
            .with("include.schema.changes", "false")
            .with("database.server.id", "10181")
            .with("database.server.name", "customer-mysql-db-server")
            .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
            .with("database.history.file.filename", "/tmp/dbhistory.dat")
        .build();
    }

}
