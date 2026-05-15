package cl.duoc.lmcartms.dtos;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleInputDTO {

    @Positive
    private Long clienteId;

    @Positive
    private Long productoId;

    @PositiveOrZero
    private Integer cantidad;


}
