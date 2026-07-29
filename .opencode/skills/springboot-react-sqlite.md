name: springboot-react-sqlite
description: Skill for the Gestion de Usuarios project — Spring Boot 3 + React + SQLite/PostgreSQL

## Project Structure

```
Gestion-de-usuarios/
├── Backend/
│   ├── build.gradle.kts        # Gradle with Spring Boot 3.3, JPA, Security, JWT, SQLite
│   ├── settings.gradle.kts
│   └── src/main/java/com/gestionusuarios/
│       ├── config/             # CORS, Security, Swagger config
│       ├── controller/         # REST controllers (Auth, User, Permission)
│       ├── dto/                # Request/Response DTOs
│       ├── entity/             # JPA entities (User, Role, Permission, PermissionRole)
│       ├── exception/          # GlobalExceptionHandler + custom exceptions
│       ├── repository/         # Spring Data JPA repositories
│       ├── security/           # JWT filter, token provider, UserDetailsService
│       └── service/            # Business logic layer
└── docs/                       # Documentation
```

## Tech Stack
- **Backend**: Java 17+, Spring Boot 3.4+, Spring Data JPA, Spring Security, JWT (jjwt 0.12+)
- **Database**: SQLite (Development) / PostgreSQL (Production via profile)
- **Frontend**: React 18+, TypeScript, Vite, Tailwind CSS, React Router v6, Axios
- **Build Tools**: Gradle (Kotlin DSL)
- **API Docs**: OpenAPI/Swagger (springdoc-openapi-ui)

## Conventions & Architecture

### Backend Conventions
- **Language**: English for all code artifacts (classes, methods, variables, database tables, and columns).
- **Naming**: `PascalCase` for classes/interfaces, `camelCase` for methods/variables, `snake_case` for database elements.
- **Layers**: Controller → Service → Repository → Entity (Strict Layered Architecture).
- **Injections**: Always use constructor injection via Lombok's `@RequiredArgsConstructor`.
- **Boilerplate**: Use Lombok annotations (`@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) effectively.

### Frontend Conventions
- **Language**: English for code (components, variables, functions, hooks) and Spanish for UI text.
- **Naming**: `PascalCase` for components/pages, `camelCase` for hooks/functions/variables, `kebab-case` for asset filenames.
- **Styles**: Tailwind utility classes directly in JSX. Use `clsx` or `tailwind-merge` for conditional classes.

## Strict Rules

### 1. Data Transfer Objects (DTOs) & Validation
- **No Entity Exposure**: Never return or accept JPA Entities in the Controller layer. Always use DTOs.
- **Validation**: Incoming DTOs must be validated using `jakarta.validation.constraints` (e.g., `@NotBlank`, `@Email`, `@Size`).
- **Trigger**: Controllers must use `@Valid` on request bodies to trigger validation rules.

### 2. Standardized API Responses
- All endpoints must return a unified JSON structure wrapper (`ApiResponse<T>`):
  ```json
  {
    "success": true,
    "message": "Operation completed successfully",
    "data": { ... },
    "timestamp": "2026-07-29T14:00:00Z"
  }
  ```
- **Error Handling**: The `GlobalExceptionHandler` must intercept all exceptions (including `MethodArgumentNotValidException`) and return this unified structure with semantic HTTP statuses (`400`, `401`, `403`, `404`, `500`).

### 3. Security & JWT
- **Public Routes**: Only `/api/v1/auth/**`, `/v3/api-docs/**`, and `/swagger-ui/**` are public. All other paths require authentication.
- **CORS**: Must explicitly permit traffic from the Frontend origin (e.g., `http://localhost:5173`).
- **Authorities**: Map user permissions into Spring Security's `GrantedAuthority` array during token parsing using the `ROLE_` or `SCOPE_` convention.

### 4. Database Multi-Profile (SQLite / Postgres)
- **SQLite Compatibility**: Strategy for IDs must be `GenerationType.IDENTITY` to avoid sequence compatibility issues with PostgreSQL.
- **Configuration**: `ddl-auto: update` is allowed only for local SQLite development. Production PostgreSQL must use environment variables for credentials.

### 5. Frontend State & Axios Interceptors
- **Auth State**: Global authentication state (user info, roles) must be managed via React Context (`AuthContext`).
- **Token Storage**: Access tokens are stored in application memory/state. If local storage is used, secure routing checks must validate expiration.
- **Axios Client**: A central Axios instance must use an **Authorization Interceptor** to automatically attach the `Authorization: Bearer <token>` header to secured endpoints.
- **Error Interceptor**: Intercept `401 Unauthorized` or `403 Forbidden` responses to clear invalid sessions and redirect users to the `/login` route.

## Execution Commands

### Running the Backend
```bash
cd Backend
# Run with local SQLite
./gradlew bootRun

# Run with PostgreSQL profile
./gradlew bootRun --args='--spring.profiles.active=postgres'
```

### Running Backend Tests
```bash
cd Backend
./gradlew test
```

### Running the Frontend
```bash
cd Frontend
npm install
npm run dev
```
