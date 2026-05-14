package cl.duoc.lmcartms.Service;

import cl.duoc.lmcartms.dtos.CarritoInputDTO;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.CarritoUpdateDTO;
import cl.duoc.lmcartms.exceptions.IdNoExisteException;
import cl.duoc.lmcartms.mappers.CarritoInputMapper;
import cl.duoc.lmcartms.mappers.CarritoResponseMapper;
import cl.duoc.lmcartms.mappers.CarritoUpdateMapper;
import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import cl.duoc.lmcartms.repositories.CarritoRepository;
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
    CarritoUpdateMapper carritoUpdateMapper;

    @Autowired
    CarritoRepository carritoRepository;

    //CREATE:
    @Transactional
    public CarritoResponseDTO agregarProducto(CarritoInputDTO dto) {

        Carrito carrito = carritoRepository.findById(dto.getClienteId()).orElseGet(Carrito::new);

        return carritoResponseMapper.toDto(carritoRepository.save(carritoInputMapper.toEntity(detalle.getClienteId(), detalle)));
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

    //UPDATE
    public CarritoResponseDTO update(CarritoUpdateDTO dto) {
        Carrito ent = carritoRepository.findById(dto.getClienteId()).orElseThrow(() -> new IdNoExisteException("ID de carrito no existe."));
        return carritoResponseMapper.toDto(carritoRepository.save(carritoUpdateMapper.toEntity(ent, dto)));
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
