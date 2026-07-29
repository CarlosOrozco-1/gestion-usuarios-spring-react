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

## 2. Probar con Postman

### Importar la colección
1. Abre Postman
2. File → Import → seleccionar `postman/gestion-usuarios.postman_collection.json`
3. Las variables (`base_url`, `token`, etc.) se cargan automáticamente

### Flujo de prueba recomendado

**Paso 1: Seed de datos (SQLite)**
La primera vez que arranca, Hibernate crea las tablas automáticamente (`ddl-auto: update`). Las tablas inician vacías.

Necesitas insertar datos iniciales para poder probar. Puedes hacerlo mediante los endpoints:

1. **Crear un rol** → `POST /api/roles`
   ```json
   { "name": "ADMIN", "description": "Administrator" }
   ```

2. **Crear permisos** → `POST /api/permissions`
   ```json
   { "name": "CREATE_USER", "description": "Can create users", "resourcePath": "/api/users" }
   ```

3. **Asignar permisos al rol** → `POST /api/roles/{id}/permissions`
   ```json
   [1]
   ```

4. **Crear un usuario** → `POST /api/users`
   ```json
   {
     "idNumber": "1234567890",
     "name": "Admin",
     "email": "admin@example.com",
     "password": "password123",
     "roleId": 1
   }
   ```

5. **Login** → `POST /api/auth/login`
   ```json
   { "email": "admin@example.com", "password": "password123" }
   ```
   El script de Postman auto-asigna el `token` para los siguientes requests.

**Paso 2: CRUD**
Una vez autenticado, prueba los endpoints de Users, Roles y Permissions desde Postman.

## 3. Cambiar a PostgreSQL
```bash
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

## 4. Detener el servidor
Presiona `Ctrl+C` en la terminal donde corre el servidor.
