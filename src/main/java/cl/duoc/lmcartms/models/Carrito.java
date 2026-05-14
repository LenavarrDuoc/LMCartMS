package cl.duoc.lmcartms.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "carritos")
public class Carrito {

    @Id
    private Long clienteId;

    @OneToMany(mappedBy = "carrito", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Setter(AccessLevel.NONE) //permite que nadie acceda a lista fuera de la clase. Solo el métido addDetalle abajo.
    private List<Detalle>  detalles =  new ArrayList<>();

    @Column(name = "fec_crea", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fec_mod")
    private LocalDateTime fechaModificacion;

    public void addDetalle(Detalle newDetalle){//corrobora si existe detalle de compra con mismo id de detalle, y con mismo id de producto. Si coincide el id de producto, suma la cantidad. Si no, agrega un nuevo detalle al carrito. Está hecho funcional en vez de por casos.
        this.detalles.stream().filter(d -> d.getProductoId().equals(newDetalle.getProductoId())).findFirst().ifPresentOrElse(d -> {d.setCantidad(d.getCantidad()+newDetalle.getCantidad());}, () -> {this.detalles.add(newDetalle);
            newDetalle.setCarrito(this);});

    }

    public Double getTotal() {
        return this.detalles.stream().mapToDouble(d -> d.getSubtotal()).sum();
    } //toma la lista detalles, stremea y mapea cada subtotal a Double, y los suma para retornar la suma.
    //Por qué no almacenar el total? porque si se va a la base de datos y ocurre un error de actualización del carrito, no reflejará bien el precio. Es mejor que pase el total a "pedido" y a "pago/boleta" y se almacene ahí.

    @PrePersist //establece fecha automáticamente antes de pasarlo a persistencia.
    protected void fecCreacion(){
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate //actualiza atributo antes de pasarlo a persistencia.
    protected void fecModificacion(){
        this.fechaModificacion = LocalDateTime.now();
    }

}
