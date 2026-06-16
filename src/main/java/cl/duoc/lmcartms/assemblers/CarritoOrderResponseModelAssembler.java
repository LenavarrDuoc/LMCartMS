package cl.duoc.lmcartms.assemblers;

import cl.duoc.lmcartms.controllers.CarritoRESTControllerV2;
import cl.duoc.lmcartms.dtos.CarritoOrderResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarritoOrderResponseModelAssembler implements RepresentationModelAssembler<CarritoOrderResponseDTO, EntityModel<CarritoOrderResponseDTO>> {

    @Override
    public EntityModel<CarritoOrderResponseDTO> toModel(CarritoOrderResponseDTO dto){
        return EntityModel.of(dto,
                linkTo(methodOn(CarritoRESTControllerV2.class).findByIdForOrder(dto.getClienteId())).withSelfRel()
                );
    }
}
