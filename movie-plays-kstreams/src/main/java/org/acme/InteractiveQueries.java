package org.acme;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import static org.apache.kafka.streams.StoreQueryParameters.fromNameAndType;

import java.util.Optional;

@ApplicationScoped
public class InteractiveQueries {
    
    @Inject
    KafkaStreams streams;

    public Optional<MoviePlayCountData> getMoviePlayCountData(int id) {
        MoviePlayCount moviePlayCount = getMoviesPlayCount().get(id);
        if (moviePlayCount != null) {
            return Optional.of(new MoviePlayCountData(moviePlayCount.name, moviePlayCount.count));
        } else {
            return Optional.empty();
        }
    }

    private ReadOnlyKeyValueStore<Integer, MoviePlayCount> getMoviesPlayCount() {
        while (true) {
            try {
                return streams.store(fromNameAndType(TopologyProducer.COUNT_MOVIE_STORE, QueryableStoreTypes.keyValueStore()));
            } catch (InvalidStateStoreException e) {
                // ignore, store not ready yet
            }
        }
    }

}
