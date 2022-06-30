package org.acme;

import java.time.Instant;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.debezium.outbox.quarkus.ExportedEvent;

public class GameEvent implements ExportedEvent<String, JsonNode> {

    private static ObjectMapper mapper = new ObjectMapper();

    private static final String TYPE = "Game";
    private static final String EVENT_TYPE = "GameCreated";

    private final long gameId;
    private final JsonNode jsonNode;
    private final Instant timestamp;

    public GameEvent(Game game) {
        this.gameId = game.id;
        this.timestamp = Instant.now();
        this.jsonNode = convertToJson(game);
    }

    private JsonNode convertToJson(Game game) {
        ObjectNode asJson = mapper.createObjectNode()
                .put("id", game.id)
                .put("name", game.name);
        
        return asJson;
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
    
}
