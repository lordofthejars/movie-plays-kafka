package org.acme;


import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jboss.logging.Logger;

@Path("/movie")
public class MovieResource {

    // Service to insert the movie data into Movie and Outbox tables
    @Inject
    MovieService movieService;

    // Injects the logger
    @Inject
    Logger logger;

    // Http Post method to insert a movie
    @POST
    public Movie insert(Movie movie) {
        logger.info("New Movie inserted " + movie.name);
        System.out.println(":)");
        
        return movieService.insertMovie(movie);
    }
}