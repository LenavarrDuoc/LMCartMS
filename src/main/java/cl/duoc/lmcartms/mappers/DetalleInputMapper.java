package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.dtos.ProductoDTO;
import cl.duoc.lmcartms.models.Detalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalleInputMapper {
    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

    public Detalle toEntity(DetalleInputDTO dto){
        Detalle ent = new Detalle();

        ent.setClienteId(dto.getClienteId());
        ent.setProductoId(dto.getProductoId());
        ent.setCantidad(dto.getCantidad());
        ProductoDTO productoDTO = toAPICatalogFeign.obtener(ent.getProductoId());
        ent.setPrecio(productoDTO.getPrecio());
        return ent;
    }
}
