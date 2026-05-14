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

    @PositiveOrZero
    private Long clienteId;

    @PositiveOrZero
    private Long productoId;

    @Positive
    private Integer cantidad;

    @Positive
    private Double precio;

}
