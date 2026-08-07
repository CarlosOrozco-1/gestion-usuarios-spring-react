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
        string business_name
        string tax_regime
        date birth_date
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
- `CLIENT` son los clientes gestionados por los usuarios del sistema. Las columnas se
  normalizan en inglés (`business_name`, `tax_regime`, `birth_date`) aunque provienen de
  `DBClientes.sql`, que conserva los nombres en español (`nombre_razon_social`,
  `regimen_fiscal`, `fecha_nacimiento`) como referencia del requisito.
- `CREDENTIAL` guarda credenciales de acceso de un cliente a sistemas externos
  (con la contraseña cifrada).
- El borrado de `SYSTEM_USER` y `CLIENT` es lógico (soft delete vía `active`).
