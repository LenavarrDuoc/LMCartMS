package cl.duoc.lmcartms.models;

import jakarta.persistence.*;
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

    private Long clienteId;

    private Long productoId;

    private Integer cantidad;

    private Double precio;

    private Double subtotal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_carrito_id")
    private Carrito carrito;

    @PrePersist
    @PreUpdate
    public void calcSubtotal() {
        if (this.precio != null && this.cantidad != null) {
            this.subtotal = this.precio * this.cantidad;
        } else {
            this.subtotal = 0.0;
        }
    }
}
