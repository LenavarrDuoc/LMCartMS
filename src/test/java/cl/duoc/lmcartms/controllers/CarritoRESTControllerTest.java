package cl.duoc.lmcartms.controllers;

import cl.duoc.lmcartms.dtos.CarritoOrderResponseDTO;
import cl.duoc.lmcartms.dtos.CarritoResponseDTO;
import cl.duoc.lmcartms.dtos.DetalleInputDTO;
import cl.duoc.lmcartms.services.CarritoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CarritoRESTController.class)
@WithMockUser(username = "testUser", roles = {"USER"}) // Simula un usuario autenticado para pasar el filtro de seguridad
public class CarritoRESTControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CarritoService carritoService;

    private DetalleInputDTO validDto;
    private CarritoResponseDTO responseDto;
    private CarritoOrderResponseDTO orderResponseDto;
    private final Long CLIENTE_ID = 1L;

    @BeforeEach
    void setUp() {
        validDto = new DetalleInputDTO(CLIENTE_ID, 100L, 2);

        responseDto = new CarritoResponseDTO();
        responseDto.setClienteId(CLIENTE_ID);
        responseDto.setTotal(5000.0);
        responseDto.setDetalles(new ArrayList<>());

        orderResponseDto = new CarritoOrderResponseDTO();
        orderResponseDto.setClienteId(CLIENTE_ID);
        orderResponseDto.setTotal(5000.0);
        orderResponseDto.setDetalles(new ArrayList<>());
    }

    // --- TESTS PARA POST (CREATE/UPDATE) ---

    @Test
    @DisplayName("POST /api/v1/carritos - Éxito retorna 201 y cabecera Location")
    void agregarProducto_ValidInput_ReturnsCreated() throws Exception {
        when(carritoService.agregarProducto(any(DetalleInputDTO.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/carritos")
                        .with(csrf()) // Inyecta un token CSRF válido simulado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/v1/carritos/" + CLIENTE_ID))
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID))
                .andExpect(jsonPath("$.total").value(5000.0));
    }

    @Test
    @DisplayName("POST /api/v1/carritos - Falla con 400 Bad Request si el DTO viola las validaciones")
    void agregarProducto_InvalidInput_ReturnsBadRequest() throws Exception {
        DetalleInputDTO invalidDto = new DetalleInputDTO(-5L, 100L, 2);

        mockMvc.perform(post("/api/v1/carritos")
                        .with(csrf()) // Inyecta un token CSRF válido simulado
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    // --- TESTS PARA GET (READ) ---

    @Test
    @DisplayName("GET /api/v1/carritos/{id} - Retorna 200 si el carrito existe")
    void findById_ExistingId_ReturnsOk() throws Exception {
        when(carritoService.findById(CLIENTE_ID)).thenReturn(responseDto);

        // Los métodos GET no requieren token CSRF, por lo que no es necesario inyectarlo aquí.
        mockMvc.perform(get("/api/v1/carritos/{id}", CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID));
    }

    @Test
    @DisplayName("GET /api/v1/carritos/{id} - Retorna 404 si el carrito no existe")
    void findById_NonExistingId_ReturnsNotFound() throws Exception {
        when(carritoService.findById(CLIENTE_ID)).thenReturn(null);

        mockMvc.perform(get("/api/v1/carritos/{id}", CLIENTE_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/v1/carritos/get-cart-for-sell-order/{id} - Retorna 200 con formato de Orden")
    void findByIdForOrder_ExistingId_ReturnsOk() throws Exception {
        when(carritoService.sendById(CLIENTE_ID)).thenReturn(orderResponseDto);

        mockMvc.perform(get("/api/v1/carritos/get-cart-for-sell-order/{id}", CLIENTE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.clienteId").value(CLIENTE_ID));
    }

    // --- TESTS PARA DELETE ---

    @Test
    @DisplayName("DELETE /api/v1/carritos/{id} - Retorna 204 No Content si se elimina con éxito")
    void deleteById_ExistingId_ReturnsNoContent() throws Exception {
        when(carritoService.deleteCarritoById(CLIENTE_ID)).thenReturn(true);

        mockMvc.perform(delete("/api/v1/carritos/{id}", CLIENTE_ID)
                        .with(csrf())) // Inyecta un token CSRF válido simulado
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/v1/carritos/{id} - Retorna 404 Not Found si el ID no existe")
    void deleteById_NonExistingId_ReturnsNotFound() throws Exception {
        when(carritoService.deleteCarritoById(CLIENTE_ID)).thenReturn(false);

        mockMvc.perform(delete("/api/v1/carritos/{id}", CLIENTE_ID)
                        .with(csrf())) // Inyecta un token CSRF válido simulado
                .andExpect(status().isNotFound());
    }
}