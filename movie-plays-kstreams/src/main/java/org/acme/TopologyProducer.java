package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.Stores;

import io.quarkus.kafka.client.serialization.ObjectMapperSerde;

@ApplicationScoped
public class TopologyProducer {
    
    private static final String MOVIES_TOPIC = "movies";
    private static final String PLAY_MOVIES_TOPIC = "playtimemovies";

    public static final String COUNT_MOVIE_STORE = "countMovieStore"; 

    @Produces
    public Topology getTopCharts() {

        final StreamsBuilder builder = new StreamsBuilder();
        
        final ObjectMapperSerde<Movie> movieSerder = new ObjectMapperSerde<>(Movie.class);
        final ObjectMapperSerde<MoviePlayed> moviePlayedSerder = new ObjectMapperSerde<>(MoviePlayed.class);
        final ObjectMapperSerde<MoviePlayCount> moviePlayCountSerder = new ObjectMapperSerde<>(MoviePlayCount.class);

        KeyValueBytesStoreSupplier storeSupplier = Stores.persistentKeyValueStore(COUNT_MOVIE_STORE);

        final GlobalKTable<Integer, Movie> moviesTable = builder.globalTable(
                MOVIES_TOPIC,
                Consumed.with(Serdes.Integer(), movieSerder));

        final KStream<String, MoviePlayed> playEvents = builder.stream(
            PLAY_MOVIES_TOPIC, Consumed.with(Serdes.String(), moviePlayedSerder));
        
            playEvents
            .filter((region, event) -> event.duration >= 10)
            .map((key, value) -> KeyValue.pair(value.id, value))
            .join(moviesTable, (movieId, moviePlayedId) -> movieId, (moviePlayed, movie) -> movie)
            .groupByKey(Grouped.with(Serdes.Integer(), movieSerder))
            .aggregate(MoviePlayCount::new, 
                        (movieId, movie, moviePlayCounter) -> moviePlayCounter.aggregate(movie.name), 
                        Materialized.<Integer, MoviePlayCount> as(storeSupplier)
                        .withKeySerde(Serdes.Integer())
                        .withValueSerde(moviePlayCountSerder)
                        )
            .toStream()
            .print(Printed.toSysOut());
        
        return builder.build();

    }

}
