package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.debezium.outbox.quarkus.ExportedEvent;

@ApplicationScoped
public class MovieService {
    
    // CDI event interface triggering Outbox entities
    @Inject
    Event<ExportedEvent<?, ?>> event;

    // Transaction method
    @Transactional
    public Movie insertMovie(Movie movie) {

        // Persists data
        movie.persist();
        
        // Persists outbox content
        event.fire(new MovieEvent(movie));
        
        return movie;
    }

    /**@Channel("movies")
    Emitter<Record<Long, String>> movieEmitter;

    private static ObjectMapper objectMapper = new ObjectMapper();
    
    @Transactional
    public Game dualWriteInsert(Game game) throws JsonProcessingException {
        // Inserts to DB
        game.persist();

        // Send an event to movies topic
        final String payloadJson = objectMapper.writeValueAsString(game);
        long id = game.id;

        movieEmitter.send(Record.of(id, payloadJson));
        return game;
    }**/

}
