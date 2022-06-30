package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import io.debezium.outbox.quarkus.ExportedEvent;

@ApplicationScoped
public class GameService {
    
    @Inject
    Event<ExportedEvent<?, ?>> event;

    @Transactional
    public Game insertGame(Game game) {
        game.persist();
        event.fire(new GameEvent(game));
        
        return game;
    }

}
