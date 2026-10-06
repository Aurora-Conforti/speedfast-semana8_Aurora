-- Script de la base de datos de SpeedFast (Semana 8)
CREATE DATABASE IF NOT EXISTS speedfast_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE speedfast_db;

CREATE TABLE IF NOT EXISTS repartidores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS pedidos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    direccion VARCHAR(100) NOT NULL,
    tipo ENUM('COMIDA','ENCOMIENDA','EXPRESS'),
    estado ENUM('PENDIENTE','EN_REPARTO','ENTREGADO')
);

CREATE TABLE IF NOT EXISTS entregas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_pedido INT,
    id_repartidor INT,
    fecha DATE,
    hora TIME,
    FOREIGN KEY (id_pedido) REFERENCES pedidos(id),
    FOREIGN KEY (id_repartidor) REFERENCES repartidores(id)
);
