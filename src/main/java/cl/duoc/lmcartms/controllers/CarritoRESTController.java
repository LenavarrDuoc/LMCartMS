package cl.duoc.lmcartms.controllers;

import cl.duoc.lmcartms.Service.CarritoService;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/carritos")
public class CarritoRESTController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoRESTController.class.getName());

    @Autowired
    private CarritoService carritoService;

    //CREATE:
    @PostMapping
    public ResponseEntity<CarritoResponseDTO> agregarProducto(@Valid @RequestBody DetalleInputDTO dto){
        String logMsgRequest = "Recibiendo solicitud para crear/guardar carrito.";
        String logMsg = "Solicitud para crear/guardar/actualizar carrito.";
        logger.info(logMsgRequest);
        CarritoResponseDTO created = carritoService.agregarProducto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getClienteId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + "=> Carrito creado/actualizado con ID carrito: {}, cantidad de productos agregados: {}, y total: ${}", created.getClienteId(), created.getDetalles().size(), created.getTotal());
        return ResponseEntity.created(location).body(created);
        //devuelve el estado y la locación //devuelve el objeto creado
    }

    //READ:
    @GetMapping("/{id}")
    public ResponseEntity<CarritoResponseDTO> findById(@PathVariable Long id){
        String logMsgRequest = "Recibiendo solicitud para buscar carrito por ID: " + id + ".";
        String logMsg = "Solicitud para buscar carrito por ID: " + id + ".";
        logger.info(logMsgRequest);
        CarritoResponseDTO dto = carritoService.findById(id);
        if (dto != null){
            logger.info(logMsg + "=> encontrado.");
            return ResponseEntity.ok(dto);
        }
        logger.info(logMsg + "=> no encontrado.");
        return ResponseEntity.notFound().build();
    }

    //DELETE:
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
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
