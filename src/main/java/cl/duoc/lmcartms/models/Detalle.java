package cl.duoc.lmcartms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Detalle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Positive
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull
    @Positive
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotNull
    @Positive
    private Integer cantidad;

    @NotNull
    @PositiveOrZero
    private Double precio;

    @NotNull
    @PositiveOrZero
    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_carrito_id")
    private Carrito carrito;

    public void calcSubtotal() {
        if (this.precio != null && this.cantidad != null) {
            this.subtotal = this.precio * this.cantidad;
        } else {
            this.subtotal = 0.0;
        }
    }
}
