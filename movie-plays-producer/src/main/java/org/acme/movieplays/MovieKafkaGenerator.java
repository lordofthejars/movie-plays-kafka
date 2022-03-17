package org.acme.movieplays;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.jboss.logging.Logger;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;

@ApplicationScoped
public class MovieKafkaGenerator {
    
    private Random random = new Random();

    @Inject
    Logger logger;

    private List<Movie> movies = List.of(
        new Movie(1, "The Hobbit", "Peter Jackson", "Fantasy"),
        new Movie(2, "Star Trek: First Contact", "Jonathan Frakes", "Space"),
        new Movie(3, "Encanto", "Jared Bush", "Animation"),
        new Movie(4, "Cruella", "Craig Gillespie", "Crime Comedy"),
        new Movie(5, "Sing 2", "Garth Jennings", "Jukebox Musical Comedy")
    );

    // Populates movies into Kafka
    @Outgoing("movies")                                          
    public Multi<Record<Integer, Movie>> movies() {
        return Multi.createFrom().items(movies.stream()
                .map(m -> Record.of(m.id, m))
        );
    }

    // Generate playing times
    @Outgoing("play-time-movies")
    public Multi<Record<String, MoviePlayed>> generate() {
        return Multi.createFrom().ticks().every(Duration.ofMillis(500))    
                .onOverflow().drop()
                .map(tick -> {
                    Movie movie = movies.get(random.nextInt(movies.size()));
                    int time = random.nextInt(300);
                    logger.infov("movie {0} played for {1} minutes", movie.name, time);
                    // Region as key
                    return Record.of("eu", new MoviePlayed(movie.id, time));
        });
    }



}
