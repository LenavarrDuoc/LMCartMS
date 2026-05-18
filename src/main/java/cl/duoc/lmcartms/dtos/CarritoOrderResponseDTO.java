package cl.duoc.lmcartms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoOrderResponseDTO {


    private Long clienteId;
    private String nombreCliente;
    private String runCliente;
    private List<DetalleOrderResponseDTO> detalles;
    private Double total; //No existe en persistencia en tabla Carrito. Lo calcula con método getTotal invocado en el mapeo.

}