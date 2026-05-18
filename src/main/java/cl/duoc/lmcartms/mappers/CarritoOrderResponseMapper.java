package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.clients.ToAPICustomerFeign;
import cl.duoc.lmcartms.dtos.CarritoOrderResponseDTO;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.ClienteOrderResponseDTO;
import cl.duoc.lmcartms.models.Carrito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

//Está un DTO distinto que entrega objetos a las APIs que lo consuman. Es igual al CarritoResponseDTO, pero se deja en caso de que se requiera modificación. Mientras que CarritoResponseDto está para mostrar al objeto creado durante .save
@Component
public class CarritoOrderResponseMapper {

    @Autowired
    DetalleOrderResponseMapper detalleOrderResponseMapper;

    @Autowired
    ToAPICustomerFeign toAPICustomerFeign;

    public CarritoOrderResponseDTO toDto(Carrito ent) {
        ClienteOrderResponseDTO clienteDto = toAPICustomerFeign.findById(ent.getClienteId());

        CarritoOrderResponseDTO dto = new CarritoOrderResponseDTO();

        dto.setClienteId(ent.getClienteId());
        dto.setNombreCliente(clienteDto.getNombre());
        dto.setRunCliente(clienteDto.getRun());
        //Stream de lista de detalles del carrito. Mapper de detalleResponse agrega datos del producto al conectarse a Producto de LMCatalogMS via FEIGN:
        dto.setDetalles(ent.getDetalles().stream().map(detalleOrderResponseMapper::toDto).toList());
        dto.setTotal(ent.getTotal());
        return dto;
    }

}
