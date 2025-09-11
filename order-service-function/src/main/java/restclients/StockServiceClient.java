package restclients;

import dtos.StandardResponse;
import dtos.StockRequest;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/items")
@RegisterRestClient(configKey = "stock-service")
public interface StockServiceClient {



    @POST
    @Path("/{productId}/reserve")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<StandardResponse> reserveStock(@PathParam("productId") String productId, StockRequest request);


    @POST
    @Path("/{productId}/release")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<StandardResponse> releaseReservation(@PathParam("productId") String productId, StockRequest request);

    @GET
    @Path("/saludo")
    Uni<StandardResponse> getSaludo();




}
