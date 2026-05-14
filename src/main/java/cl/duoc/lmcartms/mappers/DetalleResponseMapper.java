package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.dtos.DetalleResponseDTO;
import cl.duoc.lmcartms.dtos.ProductoDTO;
import cl.duoc.lmcartms.models.Detalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DetalleResponseMapper {

    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

    public DetalleResponseDTO toDto(Detalle ent) {
        DetalleResponseDTO dto = new DetalleResponseDTO();

        dto.setProductoId(ent.getProductoId());
        //Se agregan detalles desde Producto ya que Entidad detalles solo guarda id de producto en capa de persistencia:
        ProductoDTO productoDTO = toAPICatalogFeign.obtener(ent.getProductoId());
        dto.setTitulo(productoDTO.getTitulo());
        dto.setAnioPublicacion(productoDTO.getAnioPublicacion());
        dto.setIsbn(productoDTO.getIsbn());
        dto.setCantidad(ent.getCantidad());
        dto.setPrecio(ent.getPrecio());
        dto.setSubTotal(ent.getSubtotal());

        return dto;
    }
}
