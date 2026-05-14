package cl.duoc.lmcartms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Detalle extends JpaRepository<Detalle,Long> {
}
