# Backend Development Phases

## Phase 1: Project Setup ⬜
- [x] Project scaffolding (Gradle + Spring Boot)
- [x] Entities (User, Role, Permission, PermissionRole)
- [x] Repositories (Spring Data JPA)
- [x] Database config (SQLite default, PostgreSQL profile)
- [x] Error handling (GlobalExceptionHandler + custom exceptions)
- [ ] **Fix config file names**: `SecurityConfi.java` → `SecurityConfig.java`, `swaggerConfig.java` → `SwaggerConfig.java`

## Phase 2: Security Layer ⬜
- [ ] `JwtTokenProvider` — generate/validate JWT tokens
- [ ] `JwtAuthenticationFilter` — extract token from header, validate, set SecurityContext
- [ ] `UserDetailsServiceImpl` — load user by email from DB
- [ ] `SecurityConfig` — configure HttpSecurity, CORS, CSRF, password encoder
- [ ] `CorsConfig` — allowed origins configuration

## Phase 3: DTOs ⬜
- [ ] `LoginRequest` (email + password)
- [ ] `LoginResponse` (token + user info)
- [ ] `UserRequest` (create/update user DTO)
- [ ] `UserResponse` (user response DTO)
- [ ] `RoleRequest` / `RoleResponse`
- [ ] `PermissionRequest` / `PermissionResponse`

## Phase 4: Service Layer ⬜
- [ ] `AuthService` — authenticate, generate token
- [ ] `UserService` — CRUD + soft delete/reactivate + validations
- [ ] `RoleService` — CRUD + assign permissions
- [ ] `PermissionService` — CRUD

## Phase 5: Controllers ⬜
- [ ] `AuthController` — POST /api/auth/login
- [ ] `UserController` — GET/POST/PUT/DELETE /api/users, PATCH reactivate
- [ ] `PermissionController` — GET/POST/PUT/DELETE /api/permissions
- [ ] Role endpoints (if needed)

## Phase 6: Validation & Swagger ⬜
- [ ] Request DTO validation annotations (`@NotBlank`, `@Email`, etc.)
- [ ] `SwaggerConfig` — OpenAPI info + security scheme

## Phase 7: Testing ⬜
- [ ] Unit tests: Service layer (JUnit 5 + Mockito)
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
