package adapters;

import dtos.StockRequest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import services.StockService;

import java.util.Map;

@Path("/stock/v1/items")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StockItemsResource {

    private final StockService stockService;

    @Inject
    public StockItemsResource(StockService stockService) {
        this.stockService = stockService;
    }

    public record StandardResponse(int code, boolean success, String message, Object data) {}


    @GET
    @Path("saludo")
    public Uni<Response> saludoMundo() {
        StockRequest stockRequest = new StockRequest();
        return Uni.createFrom().item(Response.status(Response.Status.OK).entity(new StandardResponse(200, true, "Mensaje de saludo exitoso", stockRequest)).build());
    }

    @GET
    @Path("/{productId}")
    public Uni<Response> getAvailableStock(@PathParam("productId") String productId) {
        return stockService.getAvailableStock(productId)
                .onItem().transform(availableStock ->
                        Response.ok(new StandardResponse(200, true, "Se encontró stock disponible.", Map.of("availableStock", availableStock))).build())
                .onFailure(NotFoundException.class).recoverWithItem(Response.status(Response.Status.NOT_FOUND)
                        .entity(new StandardResponse(404, false, "Producto no encontrado.", null)).build())
                .onFailure().recoverWithItem(e -> Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new StandardResponse(500, false, "Fallo en la consulta a la base de datos.", Map.of("detailError", e.getMessage()))).build());
    }


}
