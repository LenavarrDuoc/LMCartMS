package cl.duoc.lmcartms.controllers;

import cl.duoc.lmcartms.services.CarritoService;
import cl.duoc.lmcartms.assemblers.CarritoResponseModelAssembler;
import cl.duoc.lmcartms.assemblers.CarritoOrderResponseModelAssembler;
import cl.duoc.lmcartms.dtos.CarritoOrderResponseDTO;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/carritos")
@Tag(name = "Carritos", description = "Gestión de carritos de compra.")
public class CarritoRESTControllerV2 {

    private static final Logger logger = LoggerFactory.getLogger(CarritoRESTControllerV2.class.getName());

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private CarritoOrderResponseModelAssembler carritoOrderResponseModelAssembler;

    @Autowired
    private CarritoResponseModelAssembler carritoResponseModelAssembler;

    //CREATE:
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Se ha creado registro",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EntityModel.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto al hacer solicitud (ej: carritoId ya existe)",
                    content = @Content(schema = @Schema(hidden = true))
            )
    }
    )
    @PostMapping
    @Operation(summary = "Crear carrito.", description = "Guardar un registro de nuevo carrito de compras. Solo puede existir un carrito por ID de cliente.")
    public ResponseEntity<EntityModel<CarritoResponseDTO>> agregarProducto(@Valid @RequestBody DetalleInputDTO dto){
        String logMsgRequest = "Recibiendo solicitud para crear/guardar carrito.";
        String logMsg = "Solicitud para crear/guardar/actualizar carrito.";
        logger.info(logMsgRequest);
        CarritoResponseDTO created = carritoService.agregarProducto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getClienteId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + "=> Carrito creado/actualizado con ID carrito: {}, cantidad de productos agregados: {}, y total: ${}", created.getClienteId(), created.getDetalles().size(), created.getTotal());
        return ResponseEntity.created(location).body(carritoResponseModelAssembler.toModel(created));
        //devuelve el estado y la locación //devuelve el objeto creado
    }

    //READ:
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se ha encontrado registro perteneciente a carrito según ID ingresado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se ha encontrado registro perteneciente a carrito según ID ingresado.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    }
    )
    @GetMapping("/{id}")
    @Operation(summary = "Buscar carrito por ID.", description = "Traer el registro pertenenciente a un carrito según inglés.")
    public ResponseEntity<EntityModel<CarritoResponseDTO>> findById(@Parameter(description = "ID de carrito", required = true) @PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para buscar carrito por ID: " + id + ".";
        String logMsg = "Solicitud para buscar carrito por ID: " + id + ".";
        logger.info(logMsgRequest);
        CarritoResponseDTO dto = carritoService.findById(id);
        if (dto != null){
            logger.info(logMsg + "=> encontrado.");
            return ResponseEntity.ok(carritoResponseModelAssembler.toModel(dto));
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Se ha encontrado registro perteneciente a carrito según ID ingresado.",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CarritoOrderResponseDTO.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se ha encontrado registro perteneciente a carrito según ID ingresado.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    }
    )
    @GetMapping("/get-cart-for-sell-order/{id}")
    @Operation(summary = "Buscar carrito por ID (función llamada desde servicio de venta LMSellMS).", description = "Traer el registro pertenenciente a un carrito según ID ingresado.")
    public ResponseEntity<EntityModel<CarritoOrderResponseDTO>> findByIdForOrder(@Parameter(description = "ID de carrito", required = true) @PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para buscar carrito por ID: " + id + ".";
        String logMsg = "Solicitud para buscar carrito por ID: " + id + ".";
        logger.info(logMsgRequest);
        CarritoOrderResponseDTO dto = carritoService.sendById(id);
        if (dto != null){
            logger.info(logMsg + "=> encontrado.");
            return ResponseEntity.ok(carritoOrderResponseModelAssembler.toModel(dto));
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    //DELETE:
    @ApiResponses( value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Se ha eliminado registro."
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Sintáxis incorrecta",
                    content = @Content(schema = @Schema(hidden = true))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "No se han encontrado registro de carrito según ID ingresado.",
                    content = @Content(schema = @Schema(hidden = true))
            )
    }
    )
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar carrito por ID.", description = "Eliminar el registro pertenenciente a un carrito según ID ingresado.")
    public ResponseEntity<Void> deleteById(@Parameter(description = "ID de carrito", required = true) @PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para borrar carrito con ID: " + id + ".";
        String logMsg = "Solicitud para borrar carrito con ID: " + id + ".";
        logger.info(logMsgRequest);
        if(carritoService.deleteCarritoById(id)){
            logger.info(logMsg + " => encontrado y borrado.");
            return ResponseEntity.noContent().build();
        }
        logger.info(logMsg + " => no encontrado.");
        return ResponseEntity.notFound().build();
    }



}
