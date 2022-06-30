package org.acme;


import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/game")
public class GameResource {

    @Inject
    GameService gameService;

    @Inject
    Logger logger;

    @POST
    public Game insert(Game game) {
        logger.info("New Game inserted " + game.name);
        System.out.println(":)");
        
        return gameService.insertGame(game);
    }
}