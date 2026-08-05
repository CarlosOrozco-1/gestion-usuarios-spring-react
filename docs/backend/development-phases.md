# Backend Development Phases

> El modelo actual usa **SystemUser** (usuarios del sistema con credenciales de login),
> **Client** (clientes gestionados) y **Credential** (credenciales de acceso de cada
> cliente a sistemas externos). El `User` original quedó reemplazado por `SystemUser`.

## Phase 1: Project Setup ✅
- [x] Project scaffolding (Gradle + Spring Boot)
- [x] Entities (SystemUser, Client, Credential, Role, Permission, PermissionRole)
- [x] Repositories (Spring Data JPA)
- [x] Database config (SQLite default, PostgreSQL profile)
- [x] Error handling (GlobalExceptionHandler + custom exceptions)
- [x] Config files renombrados (`SecurityConfig.java`, `SwaggerConfig.java`)

## Phase 2: Security Layer ✅
- [x] `JwtTokenProvider` — generate/validate JWT tokens
- [x] `JwtAuthenticationFilter` — extract token from header, validate, set SecurityContext
- [x] `UserDetailsServiceImpl` — load user by email from DB
- [x] `SecurityConfig` — configure HttpSecurity, CORS, CSRF, password encoder
- [x] `CorsConfig` — allowed origins configuration
- [x] Authorization por permiso vía `@PreAuthorize("hasAuthority('...')")`

## Phase 3: DTOs ✅
- [x] `LoginRequest` (email + password)
- [x] `LoginResponse` (token + user info)
- [x] `UserRequest` / `UserResponse` (system user)
- [x] `ClientRequest` / `ClientResponse`
- [x] `CredentialRequest` / `CredentialResponse`
- [x] `RoleRequest` / `RoleResponse`
- [x] `PermissionRequest` / `PermissionResponse`

## Phase 4: Service Layer ✅
- [x] `AuthService` — authenticate, generate token
- [x] `SystemUserService` — CRUD + soft delete/reactivate + validations
- [x] `ClientService` — CRUD + soft delete/reactivate + validations
- [x] `CredentialService` — CRUD + filtrar por cliente
- [x] `RoleService` — CRUD + assign permissions
- [x] `PermissionService` — CRUD

## Phase 5: Controllers ✅
- [x] `AuthController` — POST /api/auth/login
- [x] `SystemUserController` — GET/POST/PUT/DELETE /api/system-users, PATCH reactivate
- [x] `ClientController` — GET/POST/PUT/DELETE /api/clients, PATCH reactivate
- [x] `CredentialController` — GET/POST/PUT/DELETE /api/credentials, GET /client/{clientId}
- [x] `RoleController` — CRUD + assign permissions
- [x] `PermissionController` — CRUD

## Phase 6: Validation & Swagger ✅
- [x] Request DTO validation annotations (`@NotBlank`, `@Email`, etc.)
- [x] `SwaggerConfig` — OpenAPI info + security scheme (JWT bearer)

## Phase 7: Testing ✅
- [x] Unit tests: Service layer (JUnit 5 + Mockito) — 33 tests
- [ ] Integration tests: Repository layer (@DataJpaTest)
- [ ] Integration tests: Controller layer (@WebMvcTest + @MockBean)
- [ ] Security tests: Authentication flow, token validation
- [ ] Test coverage for error scenarios (404, 400, 409, 401)

## Phase 8: Production Readiness ⬜
- [ ] Environment variables for all secrets (DB, JWT secret)
- [ ] Logging configuration (Logback)
- [ ] Health endpoint / Actuator
- [ ] Rate limiting (optional)
- [ ] Audit logging (optional)
