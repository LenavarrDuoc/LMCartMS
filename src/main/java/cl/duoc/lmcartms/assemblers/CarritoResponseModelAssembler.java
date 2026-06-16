package cl.duoc.lmcartms.assemblers;

import cl.duoc.lmcartms.controllers.CarritoRESTControllerV2;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CarritoResponseModelAssembler implements RepresentationModelAssembler<CarritoResponseDTO, EntityModel<CarritoResponseDTO>> {

    @Override
    public EntityModel<CarritoResponseDTO> toModel(CarritoResponseDTO dto){
        return EntityModel.of(dto,
                linkTo(methodOn(CarritoRESTControllerV2.class).findByIdForOrder(dto.getClienteId())).withSelfRel(),
                linkTo(methodOn(CarritoRESTControllerV2.class).findById(dto.getClienteId())).withRel("find-by-id")
                );
    }
}
