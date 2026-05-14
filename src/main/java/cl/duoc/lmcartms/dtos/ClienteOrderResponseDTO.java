package cl.duoc.lmcartms.dtos;



import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "run","nombre", "email", "fono", "direcciones"})
public class ClienteOrderResponseDTO {

    private Long id;
    private String run;
    private String nombre;
    private String email;
    private String fono;
    private DireccionResponseDTO direccion;
    private LocalDate fechaNacimiento;
    private Date fechaIngreso;
    private Date fechaUltimaActualizacion;

}