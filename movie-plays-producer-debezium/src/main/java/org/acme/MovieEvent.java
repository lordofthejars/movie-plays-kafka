package org.acme;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.debezium.outbox.quarkus.ExportedEvent;

public class MovieEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    // Set the type enclosed inside the event
    private static final String TYPE = "Movie";
    // Set the event type
    private static final String EVENT_TYPE = "MovieCreated";

    private final long gameId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    // Saves Game info in the class
    public MovieEvent(Movie movie) {
        this.gameId = movie.id;
        this.timestamp = Instant.now();
        // Saves game content in a string column in JSON format
        this.jsonNode = convertToJson(movie);
    }

    @Override
    public String getAggregateId() {
        return String.valueOf(this.gameId);
    }

    @Override
    public String getAggregateType() {
        return TYPE;
    }

    @Override
    public JsonNode getPayload() {
        return jsonNode;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public String getType() {
        return EVENT_TYPE;
    }
    
    private JsonNode convertToJson(Movie movie) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", movie.id)
                .put("name", movie.name)
                .put("director", movie.director)
                .put("genre", movie.genre);
        
        return asJson;
    }

}
