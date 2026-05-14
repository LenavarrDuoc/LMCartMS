package cl.duoc.lmcartms.Service;

import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.dtos.DetalleResponseDTO;

import cl.duoc.lmcartms.exceptions.IdNoExisteException;
import cl.duoc.lmcartms.mappers.CarritoInputMapper;
import cl.duoc.lmcartms.mappers.DetalleInputMapper;
import cl.duoc.lmcartms.mappers.DetalleResponseMapper;
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
public class DetalleService {

    @Autowired
    DetalleInputMapper detalleInputMapper;

    @Autowired
    DetalleResponseMapper detalleResponseMapper;

    @Autowired
    DetalleRepository detalleRepository;

    @Autowired
    CarritoService carritoService;

    @Autowired
    CarritoRepository carritoRepository;

    @Autowired
    CarritoInputMapper carritoInputMapper;

    //CREATE:
    @Transactional
    public DetalleResponseDTO save(DetalleInputDTO dto) {
        if (dto != null) {

            if (!carritoRepository.existsById(dto.getClienteId())){
                Detalle detalle = detalleInputMapper.toEntity(dto);
                carritoService.save(detalle);
                DetalleResponseDTO responseDto = detalleResponseMapper.toDto(detalle);
            } else {
                Carrito carrito = carritoRepository.findById(dto.getClienteId()).orElse(null);

                for (Detalle d : carrito.getDetalles()) {
                    if(d.getProductoId() == dto.getProductoId()){

                    }
                }
            }

        }

    }



    //READ:
    @Transactional(readOnly = true)
    public List<DetalleResponseDTO> findAll() {
        return detalleRepository.findAll().stream().map(detalleResponseMapper::toDto).toList();
    }

    @Transactional(readOnly = true)
    public DetalleResponseDTO findById(Long id) {
        return detalleResponseMapper.toDto(detalleRepository.findById(id).orElseThrow(() -> new IdNoExisteException("ID de detalle no existe.")));
    }

    //UPDATE
    public DetalleResponseDTO update(DetalleUpdateDTO dto) {
        Detalle ent = detalleRepository.findById(dto.getClienteId()).orElseThrow(() -> new IdNoExisteException("ID de detalle no existe."));
        return detalleResponseMapper.toDto(detalleRepository.save(detalleUpdateMapper.toEntity(ent, dto)));
    }

    //DELETE:
    @Transactional
    public Boolean deleteDetalleById (Long id){
        Boolean centinela = false;
        if (detalleRepository.existsById(id)) {
            detalleRepository.deleteById(id);
            centinela = true;
        } else {
            throw new IdNoExisteException("ID de detalle no existe.");
        }
        return centinela;
    }


}
