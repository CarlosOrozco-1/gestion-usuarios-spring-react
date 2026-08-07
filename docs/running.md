# Running the Backend

## Requisitos
- Java 17+ instalado
- Puerto 8080 libre

## 1. Iniciar el servidor
```bash
cd Backend
./gradlew bootRun
```

La consola mostrará algo como:
```
Tomcat started on port 8080 (http)
Started GestionUsuariosApplication in X seconds
```

## 2. Seed de datos (SQLite)

La primera vez que arranca, Hibernate crea las tablas (`ddl-auto: update`) y
`DataSeeder` inserta automáticamente:

- 3 roles: `ADMIN`, `CONTADOR`, `ASISTENTE`
- 8 permisos (ADMIN_USERS, ADMIN_ROLES, ADMIN_PERMISSIONS, ASSIGN_PERMISSIONS,
  VIEW_CLIENTS, MANAGE_CLIENTS, VIEW_CREDENTIALS, MANAGE_CREDENTIALS)
- Usuario administrador:
  ```
  email: admin@example.com
  password: password123
  ```

El seeder solo crea lo que no exista, así que puedes correrlo las veces que quieras.

> Si cambiaste el esquema (refactor), borra `Backend/gestion_usuarios.db` y reinicia
> para regenerar las tablas limpias.

### Nota: migración de una SQLite existente (columna UNIQUE nueva)
SQLite no permite `ALTER TABLE ADD COLUMN` con `UNIQUE`. Si ya tenías una base
creada antes de agregar `clients.nit`, Hibernate (`ddl-auto: update`) agrega las
columnas normales pero omite `nit` y la app falla con `no such column: c1_0.nit`.
Para migrar sin perder datos, recrea la tabla (guarda un backup previo):

```sql
BEGIN IMMEDIATE;
CREATE TABLE clients_new (
    id integer primary key,
    id_number varchar(50) not null unique,
    nit varchar(50) unique,
    business_name varchar(200),
    tax_regime varchar(100),
    birth_date date,
    email varchar(100) not null unique,
    phone varchar(20),
    address varchar(500),
    active boolean not null,
    created_at timestamp not null,
    updated_at timestamp
);
INSERT INTO clients_new (id, id_number, nit, business_name, tax_regime, birth_date, email, phone, address, active, created_at, updated_at)
SELECT id, id_number, nit, business_name, tax_regime, birth_date, email, phone, address, active, created_at, updated_at FROM clients;
DROP TABLE clients;
ALTER TABLE clients_new RENAME TO clients;
COMMIT;
```

Si vienes de una base con las columnas en español (`nombre_razon_social`,
`regimen_fiscal`, `fecha_nacimiento`) y una columna `name` huérfana, renumera así:

```sql
ALTER TABLE clients RENAME COLUMN nombre_razon_social TO business_name;
ALTER TABLE clients RENAME COLUMN regimen_fiscal TO tax_regime;
ALTER TABLE clients RENAME COLUMN fecha_nacimiento TO birth_date;
ALTER TABLE clients DROP COLUMN name;   -- requiere SQLite 3.35+
```

Luego reinicia el backend.

## 3. Probar con Postman

### Importar la colección
1. Abre Postman
2. File → Import → seleccionar `postman/gestion-usuarios.postman_collection.json`
3. Las variables (`base_url`, `token`, etc.) se cargan automáticamente

### Flujo de prueba recomendado

**Paso 1: Login** → `POST /api/auth/login`
```json
{ "email": "admin@example.com", "password": "password123" }
```
El script de Postman auto-asigna el `token` para los siguientes requests.

**Paso 2: CRUD**
Una vez autenticado, prueba los endpoints:

| Recurso | Endpoints |
|---|---|
| System Users | `GET/POST /api/system-users`, `GET/PUT/DELETE /api/system-users/{id}`, `PATCH /api/system-users/{id}/reactivate` |
| Clients | `GET/POST /api/clients`, `GET/PUT/DELETE /api/clients/{id}`, `PATCH /api/clients/{id}/reactivate` |
| Credentials | `GET/POST /api/credentials`, `GET/PUT/DELETE /api/credentials/{id}`, `GET /api/credentials/client/{clientId}` |
| Roles | `GET/POST /api/roles`, `GET/PUT/DELETE /api/roles/{id}`, `POST /api/roles/{id}/permissions` |
| Permissions | `GET/POST /api/permissions`, `GET/PUT/DELETE /api/permissions/{id}` |

Cada endpoint requiere el permiso correspondiente (ej: `ADMIN_USERS` para
system users, `MANAGE_CLIENTS` para crear clientes).

## 4. Swagger UI
Abrir en el navegador: `http://localhost:8080/swagger-ui/index.html`

## 5. Cambiar a PostgreSQL
```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

## 6. Detener el servidor
Presiona `Ctrl+C` en la terminal donde corre el servidor.
