package cl.duoc.lmcartms.repositories;

import cl.duoc.lmcartms.models.Detalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepository extends JpaRepository<Detalle,Long> {

}
