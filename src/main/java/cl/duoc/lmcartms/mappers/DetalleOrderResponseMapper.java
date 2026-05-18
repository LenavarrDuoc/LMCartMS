package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.dtos.DetalleOrderResponseDTO;
import cl.duoc.lmcartms.models.Detalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalleOrderResponseMapper {

    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

    public DetalleOrderResponseDTO toDto(Detalle ent) {
        DetalleOrderResponseDTO dto = new DetalleOrderResponseDTO();


        dto.setProductoId(ent.getProductoId());
        dto.setCantidad(ent.getCantidad());
        dto.setPrecio(ent.getPrecio());
        dto.setSubTotal(ent.getSubtotal());

        return dto;
    }
}
