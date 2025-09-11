package services;


import dtos.PosSaleRequest;
import dtos.StandardResponse;
import entity.PosSaleEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import restclients.StockServiceClient;

import java.util.logging.Logger;

@ApplicationScoped
public class PostService {

    private final StockServiceClient stockServiceClient;
    private final Logger logger = Logger.getLogger(PostService.class.getName());


    @Inject
    public PostService(@RestClient StockServiceClient stockServiceClient) {
        this.stockServiceClient = stockServiceClient;
    }

    public Uni<StandardResponse> saludoStock() {
        return stockServiceClient.getSaludo();
    }


    @Transactional
    public Uni<PosSaleEntity> registerSale(PosSaleRequest request) {
        PosSaleEntity sale = new PosSaleEntity(request.storeId, request.productId, request.quantity);
       logger.info("Inicio de solicitud para venta");
        return stockServiceClient.commitStock(request.productId, request.quantity)
                .onItem().transformToUni(v -> sale.persistAndFlush())
                .onItem().transform(v -> sale);
    }
}
