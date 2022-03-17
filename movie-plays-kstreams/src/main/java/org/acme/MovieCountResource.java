package org.acme;

import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/movie")
public class MovieCountResource {

    @Inject
    InteractiveQueries interactiveQueries;

    @GET
    @Path("/data/{id}")
    public Response movieCountData(@PathParam("id") int id) {
        Optional<MoviePlayCountData> moviePlayCountData = interactiveQueries.getMoviePlayCountData(id);

        if (moviePlayCountData.isPresent()) {
            return Response.ok(moviePlayCountData.get()).build();
        } else {
            return Response.status(Status.NOT_FOUND.getStatusCode(),
                    "No data found for movie " + id).build();
        }

    }
}