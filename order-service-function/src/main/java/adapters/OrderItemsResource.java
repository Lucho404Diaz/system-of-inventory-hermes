package adapters;

import dtos.StandardResponse;
import dtos.StockRequest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import services.OrderService;

import java.util.logging.Logger;

@Path("/orders/v1/items")
public class OrderItemsResource {

    private final OrderService orderService;



    @Inject
    public OrderItemsResource(OrderService orderService) {
        this.orderService = orderService;
    }

    @GET
    @Path("/saludo")
    public Uni<StandardResponse> llamarAlStockService() {
        return orderService.saludoStock();
    }

    @POST
    public Uni<Response> createOrder(StockRequest request, UriInfo uriInfo) {
        return orderService.createOrder(request)
                .onItem().transform(createdOrder -> {
                    var location = uriInfo.getAbsolutePathBuilder().path(createdOrder.id.toString()).build();
                    return Response.created(location).entity(createdOrder).build();
                })
                .onFailure().recoverWithItem(error -> {
                    if (error instanceof WebApplicationException) {
                        return ((WebApplicationException) error).getResponse();
                    }
                    return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity("Error interno al procesar la orden.")
                            .build();
                });
    }
}
