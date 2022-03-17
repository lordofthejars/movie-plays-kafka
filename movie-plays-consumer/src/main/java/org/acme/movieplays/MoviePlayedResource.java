package org.acme.movieplays;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;

@Path("/movies")
public class MoviePlayedResource {
    
    @Inject
    Logger logger;

    @Incoming("movies")
    public void newMovie(Movie movie) {
        logger.infov("New movie: {0}", movie);
    }

    @Channel("movies-played")
    Multi<MoviePlayed> moviesPlayed;

    @GET
    @Produces(MediaType.SERVER_SENT_EVENTS) 
    public Multi<MoviePlayed> stream() {
        return moviesPlayed; 
    }

}
