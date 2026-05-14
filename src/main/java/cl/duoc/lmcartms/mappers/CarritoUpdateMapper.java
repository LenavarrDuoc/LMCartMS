package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.dtos.CarritoInputDTO;
import cl.duoc.lmcartms.dtos.CarritoUpdateDTO;
import cl.duoc.lmcartms.models.Carrito;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CarritoUpdateMapper {
    public Carrito toEntity(Carrito ent, CarritoUpdateDTO dto) {

        if (dto != null) {
            ent.setClienteId(dto.getClienteId());
            ent.setFechaModificacion(LocalDateTime.now());

            //Se agrega detalle de solicitud de compra a carrito:
            ent.addDetalle(dto.getDetalle());
            return ent;
        }
        return null;
    }
}
