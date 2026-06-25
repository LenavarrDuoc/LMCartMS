package cl.duoc.lmcartms.repositories;

import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class DetalleRepositoryTest {

    @Autowired
    private DetalleRepository detalleRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Carrito carritoBase;
    private Detalle detalleTest;

    @BeforeEach
    void setUp() {
        carritoBase = new Carrito();
        carritoBase.setClienteId(2002L);
        entityManager.persistAndFlush(carritoBase);

        detalleTest = new Detalle();
        detalleTest.setClienteId(2002L);
        detalleTest.setProductoId(15L);
        detalleTest.setCantidad(3);
        detalleTest.setPrecio(1000.0);
        detalleTest.calcSubtotal();
        detalleTest.setCarrito(carritoBase);

        entityManager.persistAndFlush(detalleTest);
        entityManager.clear();
    }

    @Test
    @DisplayName("findById - Debe retornar el detalle existente")
    void findById_ExistingId_ReturnsDetalle() {
        Optional<Detalle> result = detalleRepository.findById(detalleTest.getId());

        assertTrue(result.isPresent());
        assertEquals(15L, result.get().getProductoId());
        assertEquals(3000.0, result.get().getSubtotal());
    }

    @Test
    @DisplayName("save - Falla si un campo obligatorio es nulo")
    void save_NullField_ThrowsException() {
        Detalle detalleMalo = new Detalle();
        // Dejamos los campos en nulo a propósito

        // Cambiamos DataIntegrityViolationException por ConstraintViolationException
        assertThrows(jakarta.validation.ConstraintViolationException.class, () -> {
            detalleRepository.saveAndFlush(detalleMalo);
        });
    }

    @Test
    @DisplayName("deleteById - Elimina el registro correctamente")
    void deleteById_ExistingId_RemovesDetalle() {
        Long id = detalleTest.getId();

        detalleRepository.deleteById(id);
        entityManager.flush();

        Optional<Detalle> result = detalleRepository.findById(id);
        assertFalse(result.isPresent());
    }
}