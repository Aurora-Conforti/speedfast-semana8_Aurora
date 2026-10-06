-- OPCIONAL: datos de ejemplo para probar la aplicación rápidamente
USE speedfast_db;

INSERT INTO repartidores (nombre) VALUES ('Juan Pérez'), ('María González');

INSERT INTO pedidos (direccion, tipo, estado) VALUES
    ('Av. Picarte 1234', 'COMIDA', 'PENDIENTE'),
    ('Calle Arauco 456', 'ENCOMIENDA', 'EN_REPARTO'),
    ('Av. Alemania 789', 'EXPRESS', 'ENTREGADO');
