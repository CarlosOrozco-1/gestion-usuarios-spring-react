# Modelo Entidad-Relación (ER)

Base de datos relacional. Uso de Hibernate/Spring Data JPA para ORM.
Por defecto SQLite; se puede cambiar a PostgreSQL con el perfil `postgres`.

## Tablas Principales

```mermaid
erDiagram
    ROL ||--o{ SYSTEM_USER : "posee muchos"
    ROL ||--o{ PERMISSION_ROLE : "se asigna a"
    PERMISSION ||--o{ PERMISSION_ROLE : "contiene"
    CLIENT ||--o{ CREDENTIAL : "posee muchas"

    SYSTEM_USER {
        int id PK
        string id_number UK
        string name
        string email UK
        string password
        int role_id FK
        boolean active
        timestamp created_at
        timestamp updated_at
    }

    CLIENT {
        int id PK
        string id_number UK
        string nit UK
        string nombre_razon_social
        string regimen_fiscal
        date fecha_nacimiento
        string email UK
        string phone
        string address
        boolean active
        timestamp created_at
        timestamp updated_at
    }

    CREDENTIAL {
        int id PK
        int client_id FK
        string system_name
        string username
        string encrypted_password
        string url
        string notes
        timestamp created_at
        timestamp updated_at
    }

    ROL {
        int id PK
        string name UK
        string description
        string status
        timestamp created_at
    }

    PERMISSION {
        int id PK
        string name UK
        string description
        string resource_path
        timestamp created_at
    }

    PERMISSION_ROLE {
        int id PK
        int role_id FK
        int permission_id FK
    }
```

## Notas
- `SYSTEM_USER` representa los usuarios del sistema (quienes inician sesión).
- `CLIENT` son los clientes gestionados por los usuarios del sistema. Su atributo
  `nombre_razon_social` reemplaza al anterior `name` para alinearse con `DBClientes.sql`;
  `nit`, `regimen_fiscal` y `fecha_nacimiento` también provienen de ese modelo.
- `CREDENTIAL` guarda credenciales de acceso de un cliente a sistemas externos
  (con la contraseña cifrada).
- El borrado de `SYSTEM_USER` y `CLIENT` es lógico (soft delete vía `active`).
