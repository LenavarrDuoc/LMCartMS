package cl.duoc.lmcartms.services;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.clients.ToAPICustomerFeign;
import cl.duoc.lmcartms.clients.ToAPIStockFeign;
import cl.duoc.lmcartms.dtos.*;

import cl.duoc.lmcartms.exceptions.IdNoExisteException;
import cl.duoc.lmcartms.exceptions.InventarioInsuficienteException;
import cl.duoc.lmcartms.exceptions.ProductoDescontinuadoException;

import cl.duoc.lmcartms.mappers.CarritoOrderResponseMapper;
import cl.duoc.lmcartms.mappers.CarritoResponseMapper;

import cl.duoc.lmcartms.mappers.DetalleInputMapper;
import cl.duoc.lmcartms.models.Carrito;

import cl.duoc.lmcartms.repositories.CarritoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true) //tenerla readOnly = true por defecto para la clase en general asegura que los métodos no hagan cambios en la bd si uno llega a olvidar brindarle la notación @Transactional correspondiente. La notación en cada método se superpone a la general.
public class CarritoService {


    @Autowired
    CarritoResponseMapper carritoResponseMapper;

    @Autowired
    CarritoOrderResponseMapper carritoOrderResponseMapper;

    @Autowired
    DetalleInputMapper detalleInputMapper;

    @Autowired
    CarritoRepository carritoRepository;

    @Autowired
    ToAPICustomerFeign toAPICustomerFeign;

    @Autowired
    ToAPICatalogFeign toAPICatalogFeign;

    @Autowired
    private ToAPIStockFeign toAPIStockFeign;

    //CREATE-UPDATE:
    @Transactional
    public CarritoResponseDTO agregarProducto(DetalleInputDTO dto) {

        //Validación de detalle:
        //Validación de ID cliente:
        if(!toAPICustomerFeign.existsById(dto.getClienteId())) {
            throw new IdNoExisteException("El cliente no existe.");
        }

        if(!toAPICatalogFeign.existsById(dto.getProductoId())) {
            throw new IdNoExisteException("El producto no existe.");
        }

        InventarioResponseDTO inventarioResponseDTO = toAPIStockFeign.findById(dto.getProductoId());
        Integer actualStock = inventarioResponseDTO.getCantidad() ;
        if(actualStock < dto.getCantidad()) {
            throw new InventarioInsuficienteException("Cantidad insuficiente en inventario. Máximo disponible actual: " + actualStock + " unidades.");
        }

        if(!inventarioResponseDTO.getEstado()) {
            throw new ProductoDescontinuadoException("Producto Descontinuado.");
        }

        //Agregar una validación en que: si la cantidad ingresada es 0, elimine el detalle (de existir en persistencia) o no lo agregue (de no existir). Si eliminar detalle implica dejar carrito sin detalles, eliminar también el carrito.


        //La siguiente linea crea una entidad carrito que toma un carrito ya existente en persistencia (lo busca a través del id de cliente que le damos). De no existir, crea un carrito con el id de cliente dado. Una vez creado, se mapeará con el detalle correspondiente.
        Carrito carrito = carritoRepository.findById(dto.getClienteId()).orElseGet(() -> {Carrito newCarrito = new Carrito(); newCarrito.setClienteId(dto.getClienteId()); return newCarrito;});

        if (dto.getCantidad().equals(0)) {
            carrito.removeDetalle(detalleInputMapper.toEntity(dto));
            if (carrito.getDetalles().isEmpty()) {
                //Armamos un carritoDTO con detalles y lista vacía antes de borrar el carrito entidad.
                CarritoResponseDTO carritoDTO = new CarritoResponseDTO();
                carritoDTO.setClienteId(dto.getClienteId());
                carritoDTO.setNombreCliente(null);
                carritoDTO.setRunCliente(null);
                carritoDTO.setDetalles(new ArrayList<>());
                carritoDTO.setTotal(0.0);
                carritoRepository.delete(carrito);
                return carritoDTO;
            }
        } else {
            carrito.addDetalle(detalleInputMapper.toEntity(dto));

        }

        return carritoResponseMapper.toDto(carritoRepository.save(carrito));
    }

    //READ:
    @Transactional(readOnly = true)
    public List<CarritoResponseDTO> findAll() {

        return carritoRepository.findAll().stream().map(carritoResponseMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public CarritoResponseDTO findById(Long id) {
        return carritoResponseMapper.toDto(carritoRepository.findById(id).orElseThrow(() -> new IdNoExisteException("ID de carrito no existe.")));
    }

    @Transactional(readOnly = true)
    public CarritoOrderResponseDTO sendById(Long id) {
        
        return carritoOrderResponseMapper.toDto(carritoRepository.findById(id).orElseThrow(() -> new IdNoExisteException("ID de carrito no existe.")));
    }

    //DELETE:
    @Transactional
    public Boolean deleteCarritoById (Long id){
        Boolean centinela = false;
        if (carritoRepository.existsById(id)) {
            carritoRepository.deleteById(id);
            centinela = true;
        } else {
            throw new IdNoExisteException("ID de carrito no existe.");
        }
        return centinela;
    }

}
