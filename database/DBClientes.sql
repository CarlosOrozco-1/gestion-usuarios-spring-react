-- LLAVES FORÁNEAS 
PRAGMA foreign_keys = ON;

-- TABLA: usuarios (Login)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL,
    correo TEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    rol TEXT NOT NULL DEFAULT 'CONTADOR',
    estado INTEGER NOT NULL DEFAULT 1, -- 1 = Activo, 0 = Inactivo
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
);

--TABLA: clientes (Información general)
CREATE TABLE IF NOT EXISTS clientes (
    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nit TEXT NOT NULL UNIQUE,
    nombre_razon_social TEXT NOT NULL,
    regimen_fiscal TEXT NOT NULL,
    fecha_nacimiento TEXT NULL,            -- Formato AAAA-MM-DD (NULL si no tiene)
    correo_electronico TEXT NULL,          -- NULL si el cliente no proporciona correo
    estado TEXT NOT NULL DEFAULT 'ACTIVO', -- 'ACTIVO' o 'INACTIVO'
    creado_en DATETIME DEFAULT CURRENT_TIMESTAMP
);

--TABLA: credenciales_clientes (Modal de contraseñas)
CREATE TABLE IF NOT EXISTS credenciales_clientes (
    id_credencial INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente INTEGER NOT NULL UNIQUE,
    pass_agencia_virtual TEXT NULL,        -- Encriptado (NULL si no se ingresa)
    pass_fel TEXT NULL,                    -- Encriptado (NULL si no se ingresa)
    pass_correo TEXT NULL,                 -- Encriptado (NULL si el cliente no dio acceso)
    
    -- Relación 1 a 1 con la tabla clientes
    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente) ON DELETE CASCADE
);