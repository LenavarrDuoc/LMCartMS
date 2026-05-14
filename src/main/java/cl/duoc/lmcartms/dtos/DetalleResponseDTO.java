package cl.duoc.lmcartms.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleResponseDTO {

    private Long productoId;
    private String titulo;
    private Integer anioPublicacion;
    private String isbn;
    private Integer cantidad;
    private Double precio;
    private Double subTotal;

}
