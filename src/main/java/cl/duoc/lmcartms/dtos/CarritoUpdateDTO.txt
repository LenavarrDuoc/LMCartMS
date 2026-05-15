package cl.duoc.lmcartms.dtos;

import cl.duoc.lmcartms.models.Detalle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoUpdateDTO {

    @NotNull
    @Positive
    private Long clienteId;

    private Detalle detalle;

}
