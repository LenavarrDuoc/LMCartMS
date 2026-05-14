package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.models.Carrito;
import org.springframework.stereotype.Component;

@Component
public class CarritoResponseMapper {

    public CarritoResponseDTO toDto(Carrito ent) {
        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setClienteId(ent.getClienteId());
        //Stream de lista de detalles del carrigo. Mapper de detalleResponse agrega datos del producto al conectarse a Producto de LMCatalogMS via FEIGN:
        DetalleResponseMapper detalleResponseMapper = new DetalleResponseMapper();
        dto.setDetalles(ent.getDetalles().stream().map(detalleResponseMapper::toDto).toList());
        dto.setTotal(ent.getTotal());
        return dto;
    }

}
