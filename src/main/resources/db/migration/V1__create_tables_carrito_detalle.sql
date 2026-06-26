-- 1. Crear la tabla de carritos primero (Note que no lleva AUTO_INCREMENT)
CREATE TABLE carritos (
                          cliente_id BIGINT PRIMARY KEY, -- El ID del cliente actúa como Clave Primaria directa
                          fec_crea TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
                          fec_mod TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NULL
);

-- 2. Crear la tabla de detalles (Lado "Muchos" con FK hacia carritos)
CREATE TABLE detalle (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         cliente_id BIGINT NOT NULL,
                         producto_id BIGINT NOT NULL,
                         cantidad INT NOT NULL,
                         precio DOUBLE NOT NULL,
                         subtotal DOUBLE NOT NULL,
                         fk_carrito_id BIGINT NOT NULL,

    -- Restricción de Clave Foránea con eliminación en cascada
                         CONSTRAINT fk_detalle_carrito FOREIGN KEY (fk_carrito_id)
                             REFERENCES carritos(cliente_id) ON DELETE CASCADE
);