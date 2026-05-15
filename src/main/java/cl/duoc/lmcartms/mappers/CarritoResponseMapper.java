package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.clients.ToAPICustomerFeign;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.ClienteOrderResponseDTO;
import cl.duoc.lmcartms.models.Carrito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CarritoResponseMapper {

    @Autowired
    DetalleResponseMapper detalleResponseMapper;

    @Autowired
    ToAPICustomerFeign toAPICustomerFeign;

    public CarritoResponseDTO toDto(Carrito ent) {
        ClienteOrderResponseDTO clienteDto = toAPICustomerFeign.findById(ent.getClienteId());

        CarritoResponseDTO dto = new CarritoResponseDTO();
        dto.setClienteId(ent.getClienteId());
        dto.setNombreCliente(clienteDto.getNombre());
        dto.setRunCliente(clienteDto.getRun());
        //Stream de lista de detalles del carrito. Mapper de detalleResponse agrega datos del producto al conectarse a Producto de LMCatalogMS via FEIGN:
        dto.setDetalles(ent.getDetalles().stream().map(detalleResponseMapper::toDto).toList());
        dto.setTotal(ent.getTotal());
        return dto;
    }

}
