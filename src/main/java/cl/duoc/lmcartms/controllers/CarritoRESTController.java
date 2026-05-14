package cl.duoc.lmcartms.controllers;

import cl.duoc.lmcartms.Service.CarritoService;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/carritos")
public class CarritoRESTController {

    private static final Logger logger = LoggerFactory.getLogger(CarritoRESTController.class.getName());

    @Autowired
    private CarritoService carritoService;

    //CREATE:

    @Autowired
    public ResponseEntity<CarritoResponseDTO> agregarProducto(@Valid @RequestBody DetalleInputDTO dto){
        String logMsgRequest = "Recibiendo solicitud para crear/guardar carrito.";
        String logMsg = "Solicitud para crear/guardar/actualizar carrito.";
        logger.info(logMsgRequest);
        CarritoResponseDTO created = carritoService.agregarProducto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(created.getId()).toUri();
        //de componentes de constructor URI // de la actual request //ruta de id // sacar la id del obj creado // transformar a URI.
        logger.info(logMsg + "=> creado con ID: {}, R.U.N.: {}, correo: {}, fono: {}.", created.getId(), created.getRun(), created.getEmail(), created.getFono());
        return ResponseEntity.created(location).body(created);
        //devuelve el estado y la locación //devuelve el objeto creado
    }

}
