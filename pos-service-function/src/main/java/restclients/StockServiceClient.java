package restclients;

import dtos.StandardResponse;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/v1/items")
@RegisterRestClient(configKey = "stock-service")
public interface StockServiceClient {


    @POST
    @Path("/{productId}/commit")
    @Produces(MediaType.APPLICATION_JSON)
    Uni<Void> commitStock(@PathParam("productId") String productId, int quantity);


    @GET
    @Path("/saludo")
    Uni<StandardResponse> getSaludo();

}
