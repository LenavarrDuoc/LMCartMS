package cl.duoc.lmcartms.repositories;

import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetalleRepository extends JpaRepository<DetalleRepository,Long> {

}
