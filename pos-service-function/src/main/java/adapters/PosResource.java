package adapters;

import dtos.PosSaleRequest;
import dtos.StandardResponse;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import services.PostService;

import java.util.logging.Logger;

@Path("/pos/v1/sales")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PosResource {

    private final PostService posService;
    private final Logger logger = Logger.getLogger(PosResource.class.getName());

    @Inject
    public PosResource(PostService postervice) {
        this.posService = postervice;
    }

    @GET
    @Path("/saludo")
    public Uni<StandardResponse> llamarAlStockService() {
        return posService.saludoStock();
    }

    @POST
    public Uni<Response> createPosSale(PosSaleRequest request, UriInfo uriInfo) {
        logger.info("Inicio de solicitud para venta");
        return posService.registerSale(request)
                .onItem().transform(createdSale -> {
                    var location = uriInfo.getAbsolutePathBuilder().path(createdSale.id.toString()).build();
                    logger.info("Venta registrada exitosamente.");
                    StandardResponse responsePayload = new StandardResponse(201, true, "Venta registrada exitosamente.", createdSale);
                    return Response.created(location).entity(responsePayload).build();
                })

                .onFailure().recoverWithItem(error -> {
                    StandardResponse errorPayload;
                    Response.Status status;
                    if (error instanceof WebApplicationException) {
                        logger.severe("Error al procesar la venta: " + error.getMessage());
                        status = Response.Status.fromStatusCode(((WebApplicationException) error).getResponse().getStatus());
                        errorPayload = new StandardResponse(status.getStatusCode(), false, error.getMessage(), null);
                    } else {
                        logger.severe("Error interno al procesar la venta: " + error.getMessage());
                        status = Response.Status.INTERNAL_SERVER_ERROR;
                        errorPayload = new StandardResponse(status.getStatusCode(), false, "Error interno al procesar la venta.", null);
                    }
                    logger.severe("Error al procesar la venta: " + error.getMessage());
                    return Response.status(status).entity(errorPayload).build();
                });
    }
}
