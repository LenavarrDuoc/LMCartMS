package cl.duoc.lmcartms.mappers;

import cl.duoc.lmcartms.dtos.CarritoInputDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CarritoInputMapper {

    @Autowired
    DetalleInputMapper detalleInputMapper;

    public Carrito toEntity(Long id, DetalleInputDTO detalle) {

        if (id == null) return null;
        Carrito ent = new Carrito();
        ent.setClienteId(id);

        //Se agrega detalle de solicitud de compra a carrito:
        ent.addDetalle(detalleInputMapper.toEntity(detalle));
        return ent;

    }
}
