package cl.duoc.lmcartms.Service;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.clients.ToAPICustomerFeign;
import cl.duoc.lmcartms.clients.ToAPIStockFeign;
import cl.duoc.lmcartms.dtos.CarritoInputDTO;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.CarritoUpdateDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.exceptions.IdNoExisteException;
import cl.duoc.lmcartms.exceptions.InventarioInsuficienteException;
import cl.duoc.lmcartms.exceptions.ProductoDescontinuadoException;
import cl.duoc.lmcartms.mappers.CarritoInputMapper;
import cl.duoc.lmcartms.mappers.CarritoResponseMapper;
import cl.duoc.lmcartms.mappers.CarritoUpdateMapper;
import cl.duoc.lmcartms.mappers.DetalleInputMapper;
import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import cl.duoc.lmcartms.repositories.CarritoRepository;
import cl.duoc.lmcartms.repositories.DetalleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CarritoService {

    @Autowired
    CarritoInputMapper carritoInputMapper;

    @Autowired
    CarritoResponseMapper carritoResponseMapper;

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
        Integer actualStock = toAPIStockFeign.findById(dto.getProductoId()).getCantidad() ;
        if(actualStock < dto.getCantidad()) {
            throw new InventarioInsuficienteException("Cantidad insuficiente en inventario. Máximo disponible actual: " + actualStock + " unidades.");
        }

        if(!toAPIStockFeign.findById(dto.getProductoId()).getEstado()) {
            throw new ProductoDescontinuadoException("Producto Descontinuado.");
        }


        //La siguiente linea crea una entidad carrito que toma un carrito ya existente en persistencia (lo busca a través del id de cliente que le damos). De no existir, crea un carrito con el id de cliente dado. Una vez creado, se mapeará con el detalle correspondiente.
        Carrito carrito = carritoRepository.findById(dto.getClienteId()).orElseGet(() -> {Carrito newCarrito = new Carrito(); newCarrito.setClienteId(dto.getClienteId()); return newCarrito;});

        carrito.addDetalle(detalleInputMapper.toEntity(dto));

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

//    //UPDATE //Mantenido solo en caso de requerir algo así.
//    public CarritoResponseDTO update(CarritoUpdateDTO dto) {
//        Carrito ent = carritoRepository.findById(dto.getClienteId()).orElseThrow(() -> new IdNoExisteException("ID de carrito no existe."));
//        return carritoResponseMapper.toDto(carritoRepository.save(carritoUpdateMapper.toEntity(ent, dto)));
//    }

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
