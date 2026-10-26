-- 2. Seleccionar la base de datos para usarla
USE hospital_db;
-- 3. Crear la tabla para guardar pacientes
CREATE TABLE pacientes (
id INT AUTO_INCREMENT PRIMARY KEY,
nombre VARCHAR(100) NOT NULL,
apellido varchar(100) not NULL,
dpi varchar(100) not null,
edad varchar(100) NOT NULL,
telefono varchar(100) not null,
motivo varchar(100) not null,
urgencia VARCHAR(50) NOT NULL
);
SELECT * FROM pacientes