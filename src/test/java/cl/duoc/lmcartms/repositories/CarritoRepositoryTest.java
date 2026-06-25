package cl.duoc.lmcartms.repositories;

import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class CarritoRepositoryTest {

    @Autowired
    private CarritoRepository carritoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Carrito carritoTest;
    private final Long CLIENTE_ID = 1001L;

    @BeforeEach
    void setUp() {
        carritoTest = new Carrito();
        carritoTest.setClienteId(CLIENTE_ID);

        Detalle detalle1 = new Detalle();
        detalle1.setClienteId(CLIENTE_ID);
        detalle1.setProductoId(50L);
        detalle1.setCantidad(2);
        detalle1.setPrecio(1500.0);
        // calcSubtotal() se ejecuta internamente dentro de addDetalle()
        carritoTest.addDetalle(detalle1);

        entityManager.persistAndFlush(carritoTest);
        entityManager.clear(); // Limpiamos caché para forzar búsquedas reales a la BD
    }

    @Test
    @DisplayName("findById - Debe recuperar el carrito, detalles y calcular el total")
    void findById_ExistingCarrito_ReturnsCarritoWithDetails() {
        Optional<Carrito> resultadoOpt = carritoRepository.findById(CLIENTE_ID);

        assertTrue(resultadoOpt.isPresent());
        Carrito carritoResult = resultadoOpt.get();

        assertEquals(CLIENTE_ID, carritoResult.getClienteId());
        assertNotNull(carritoResult.getFechaCreacion(), "La fecha debió asignarse con @PrePersist");
        assertEquals(1, carritoResult.getDetalles().size());
        assertEquals(3000.0, carritoResult.getTotal());
    }

    @Test
    @DisplayName("save - Debe insertar en cascada al agregar un nuevo detalle")
    void save_NewDetalle_PersistsOnCascade() {
        Carrito carritoBD = carritoRepository.findById(CLIENTE_ID).orElseThrow();

        Detalle detalle2 = new Detalle();
        detalle2.setClienteId(CLIENTE_ID);
        detalle2.setProductoId(99L);
        detalle2.setCantidad(1);
        detalle2.setPrecio(5000.0);

        carritoBD.addDetalle(detalle2);
        carritoRepository.saveAndFlush(carritoBD); // Forzamos actualización

        entityManager.clear();
        Carrito carritoActualizado = carritoRepository.findById(CLIENTE_ID).orElseThrow();

        assertEquals(2, carritoActualizado.getDetalles().size());
        assertEquals(8000.0, carritoActualizado.getTotal()); // 3000 + 5000
    }

    @Test
    @DisplayName("removeDetalle - Debe eliminar de la base de datos (Orphan Removal)")
    void removeDetalle_TriggersOrphanRemoval() {
        Carrito carritoBD = carritoRepository.findById(CLIENTE_ID).orElseThrow();

        // Creamos un objeto dummy solo con el ID de producto para que el .equals funcione
        Detalle detalleARemover = new Detalle();
        detalleARemover.setProductoId(50L);

        carritoBD.removeDetalle(detalleARemover);
        carritoRepository.saveAndFlush(carritoBD);

        entityManager.clear();
        Carrito carritoVacio = carritoRepository.findById(CLIENTE_ID).orElseThrow();

        assertTrue(carritoVacio.getDetalles().isEmpty());
        assertEquals(0.0, carritoVacio.getTotal());
    }
}