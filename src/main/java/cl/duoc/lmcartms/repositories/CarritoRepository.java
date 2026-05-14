package cl.duoc.lmcartms.repositories;

import cl.duoc.lmcartms.models.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito,Long> {
}
