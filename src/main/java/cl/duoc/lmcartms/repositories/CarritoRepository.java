package cl.duoc.lmcartms.entities;

import cl.duoc.lmcartms.models.Carrito;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoEntity extends JpaRepository<Carrito,Long> {
}
