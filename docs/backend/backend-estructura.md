# Arquitectura del Backend (Spring Boot 3 + Java 17)

Este proyecto sigue una arquitectura en capas para mantener separación de responsabilidades, escalabilidad y testabilidad.

## 📂 Estructura de Carpetas Explicada

| Carpeta | Responsabilidad | Qué contiene |
|---|---|---|
| `GestionUsuariosApplication.java` | Punto de entrada | Arranca el contexto de Spring. Sin esta clase, la app no inicia. |
| `config/` | Configuración global | SecurityConfig (rutas públicas/privadas), CorsConfig (permitir peticiones desde React) y SwaggerConfig (documentación automática). |
| `entity/` | Modelo de datos | Clases Java que mapean tablas SQL (`@Entity`, `@Table`). Contiene las 4 entidades del dominio. |
| `repository/` | Acceso a BD | Interfaces que extienden `JpaRepository`. Spring genera el código SQL automáticamente. |
| `dto/` | Transferencia de datos | Objetos ligeros para enviar/recibir JSON sin exponer entidades internas o contraseñas. |
| `service/` | Lógica de negocio | Interfaz + implementación (`Impl`). Aquí va la lógica pura: hashing BCrypt, validaciones complejas, reglas de permisos. |
| `controller/` | Capa HTTP (REST) | Endpoints que reciben peticiones del Frontend React. Solo enrutan al Service y devuelven JSON. |
| `security/` | Autenticación JWT | Filtros que interceptan cada petición, validan el token Bearer y generan tokens al hacer login. |
| `exception/` | Manejo de errores | Captura global (`@ControllerAdvice`) para devolver errores HTTP estandarizados en vez de pantallas blancas. |

## 🧩 Flujo de una Petición
```text
React (Frontend) 
   → POST /api/auth/login (JSON body)
      → AuthController recibe la petición
         → AuthService valida credenciales + genera JWT
            → JwtTokenProvider firma el token con BCrypt + RSA
              → ResponseEntity<LoginResponse> retorna { "token": "eyJ..." }
