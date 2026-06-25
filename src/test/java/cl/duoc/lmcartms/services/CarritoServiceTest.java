package cl.duoc.lmcartms.services;

import cl.duoc.lmcartms.clients.ToAPICatalogFeign;
import cl.duoc.lmcartms.clients.ToAPICustomerFeign;
import cl.duoc.lmcartms.clients.ToAPIStockFeign;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.dtos.InventarioResponseDTO;
import cl.duoc.lmcartms.exceptions.IdNoExisteException;
import cl.duoc.lmcartms.exceptions.InventarioInsuficienteException;
import cl.duoc.lmcartms.exceptions.ProductoDescontinuadoException;
import cl.duoc.lmcartms.mappers.CarritoOrderResponseMapper;
import cl.duoc.lmcartms.mappers.CarritoResponseMapper;
import cl.duoc.lmcartms.mappers.DetalleInputMapper;
import cl.duoc.lmcartms.models.Carrito;
import cl.duoc.lmcartms.models.Detalle;
import cl.duoc.lmcartms.repositories.CarritoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarritoServiceTest {

    @InjectMocks
    private CarritoService carritoService;

    @Mock
    private CarritoRepository carritoRepository;
    @Mock
    private ToAPICustomerFeign toAPICustomerFeign;
    @Mock
    private ToAPICatalogFeign toAPICatalogFeign;
    @Mock
    private ToAPIStockFeign toAPIStockFeign;
    @Mock
    private CarritoResponseMapper carritoResponseMapper;
    @Mock
    private CarritoOrderResponseMapper carritoOrderResponseMapper;
    @Mock
    private DetalleInputMapper detalleInputMapper;

    private DetalleInputDTO inputDTO;
    private InventarioResponseDTO inventarioDTO;
    private Carrito carritoMock;
    private Detalle detalleMock;
    private final Long CLIENTE_ID = 1L;
    private final Long PRODUCTO_ID = 100L;

    @BeforeEach
    void setUp() {
        inputDTO = new DetalleInputDTO(CLIENTE_ID, PRODUCTO_ID, 2);

        inventarioDTO = new InventarioResponseDTO();
        inventarioDTO.setProductoId(PRODUCTO_ID);
        inventarioDTO.setCantidad(10);
        inventarioDTO.setEstado(true);

        carritoMock = new Carrito();
        carritoMock.setClienteId(CLIENTE_ID);

        detalleMock = new Detalle();
        detalleMock.setProductoId(PRODUCTO_ID);
        detalleMock.setCantidad(2);
    }

    // --- TESTS PARA AGREGAR PRODUCTO (CREATE/UPDATE) ---

    @Test
    @DisplayName("agregarProducto - Falla si el cliente no existe")
    void agregarProducto_ClienteNoExiste_ThrowsException() {
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(false);

        assertThrows(IdNoExisteException.class, () -> carritoService.agregarProducto(inputDTO));

        verifyNoInteractions(toAPICatalogFeign, toAPIStockFeign, carritoRepository);
    }

    @Test
    @DisplayName("agregarProducto - Falla si el producto no existe en el catálogo")
    void agregarProducto_ProductoNoExiste_ThrowsException() {
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(true);
        when(toAPICatalogFeign.existsById(PRODUCTO_ID)).thenReturn(false);

        assertThrows(IdNoExisteException.class, () -> carritoService.agregarProducto(inputDTO));
    }

    @Test
    @DisplayName("agregarProducto - Falla si el stock es insuficiente")
    void agregarProducto_StockInsuficiente_ThrowsException() {
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(true);
        when(toAPICatalogFeign.existsById(PRODUCTO_ID)).thenReturn(true);

        inventarioDTO.setCantidad(1); // Stock actual menor que el solicitado (2)
        when(toAPIStockFeign.findById(PRODUCTO_ID)).thenReturn(inventarioDTO);

        assertThrows(InventarioInsuficienteException.class, () -> carritoService.agregarProducto(inputDTO));
    }

    @Test
    @DisplayName("agregarProducto - Falla si el producto está descontinuado")
    void agregarProducto_ProductoDescontinuado_ThrowsException() {
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(true);
        when(toAPICatalogFeign.existsById(PRODUCTO_ID)).thenReturn(true);

        inventarioDTO.setEstado(false); // Descontinuado
        when(toAPIStockFeign.findById(PRODUCTO_ID)).thenReturn(inventarioDTO);

        assertThrows(ProductoDescontinuadoException.class, () -> carritoService.agregarProducto(inputDTO));
    }

    @Test
    @DisplayName("agregarProducto - Éxito al agregar a un carrito existente (Cantidad > 0)")
    void agregarProducto_Success_AgregaDetalle() {
        // Arrange de Feign Clients
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(true);
        when(toAPICatalogFeign.existsById(PRODUCTO_ID)).thenReturn(true);
        when(toAPIStockFeign.findById(PRODUCTO_ID)).thenReturn(inventarioDTO);

        // Arrange de Repositorio y Mappers
        when(carritoRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(carritoMock));
        when(detalleInputMapper.toEntity(inputDTO)).thenReturn(detalleMock);
        when(carritoRepository.save(any(Carrito.class))).thenReturn(carritoMock);

        CarritoResponseDTO responseEsperada = new CarritoResponseDTO();
        when(carritoResponseMapper.toDto(carritoMock)).thenReturn(responseEsperada);

        // Act
        CarritoResponseDTO resultado = carritoService.agregarProducto(inputDTO);

        // Assert
        assertNotNull(resultado);
        verify(carritoRepository).save(carritoMock);
        assertEquals(1, carritoMock.getDetalles().size(), "El detalle debió añadirse al carrito");
    }

    @Test
    @DisplayName("agregarProducto - Cantidad Cero elimina detalle y elimina carrito si queda vacío")
    void agregarProducto_CantidadCero_EliminaCarritoVacio() {
        // Modificamos el DTO para enviar cantidad 0
        inputDTO.setCantidad(0);

        // El carrito inicial tiene 1 detalle (el que vamos a borrar)
        carritoMock.addDetalle(detalleMock);

        // Arrange de Feign Clients
        when(toAPICustomerFeign.existsById(CLIENTE_ID)).thenReturn(true);
        when(toAPICatalogFeign.existsById(PRODUCTO_ID)).thenReturn(true);
        when(toAPIStockFeign.findById(PRODUCTO_ID)).thenReturn(inventarioDTO);

        // Arrange de Repositorio y Mappers
        when(carritoRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(carritoMock));
        when(detalleInputMapper.toEntity(inputDTO)).thenReturn(detalleMock);

        // Act
        CarritoResponseDTO resultado = carritoService.agregarProducto(inputDTO);

        // Assert
        verify(carritoRepository).delete(carritoMock); // Verifica que se llamó a delete
        verify(carritoRepository, never()).save(any()); // Verifica que NO se guardó

        assertNotNull(resultado);
        assertEquals(CLIENTE_ID, resultado.getClienteId());
        assertTrue(resultado.getDetalles().isEmpty());
        assertEquals(0.0, resultado.getTotal());
    }

    // --- TESTS PARA READ (FIND ALL / FIND BY ID) ---

    @Test
    @DisplayName("findAll - Retorna lista de carritos mapeados")
    void findAll_ReturnsListOfDTOs() {
        when(carritoRepository.findAll()).thenReturn(List.of(carritoMock));
        when(carritoResponseMapper.toDto(carritoMock)).thenReturn(new CarritoResponseDTO());

        List<CarritoResponseDTO> resultado = carritoService.findAll();

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(carritoRepository).findAll();
    }

    @Test
    @DisplayName("findById - Retorna CarritoResponseDTO si existe")
    void findById_ExistingId_ReturnsDTO() {
        when(carritoRepository.findById(CLIENTE_ID)).thenReturn(Optional.of(carritoMock));
        when(carritoResponseMapper.toDto(carritoMock)).thenReturn(new CarritoResponseDTO());

        CarritoResponseDTO resultado = carritoService.findById(CLIENTE_ID);

        assertNotNull(resultado);
        verify(carritoRepository).findById(CLIENTE_ID);
    }

    @Test
    @DisplayName("findById - Lanza excepción si no existe")
    void findById_NonExistingId_ThrowsException() {
        when(carritoRepository.findById(CLIENTE_ID)).thenReturn(Optional.empty());

        assertThrows(IdNoExisteException.class, () -> carritoService.findById(CLIENTE_ID));
    }

    // --- TESTS PARA DELETE ---

    @Test
    @DisplayName("deleteCarritoById - Retorna true si elimina con éxito")
    void deleteCarritoById_ExistingId_ReturnsTrue() {
        when(carritoRepository.existsById(CLIENTE_ID)).thenReturn(true);

        Boolean resultado = carritoService.deleteCarritoById(CLIENTE_ID);

        assertTrue(resultado);
        verify(carritoRepository).deleteById(CLIENTE_ID);
    }

    @Test
    @DisplayName("deleteCarritoById - Lanza excepción si ID no existe")
    void deleteCarritoById_NonExistingId_ThrowsException() {
        when(carritoRepository.existsById(CLIENTE_ID)).thenReturn(false);

        assertThrows(IdNoExisteException.class, () -> carritoService.deleteCarritoById(CLIENTE_ID));
        verify(carritoRepository, never()).deleteById(anyLong());
    }
}