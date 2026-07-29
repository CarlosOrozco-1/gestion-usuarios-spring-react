# Testing

## Ejecutar todos los tests
```bash
cd Backend
./gradlew test
```

## Ver resultados en HTML
Abrir en el navegador:
```
Backend/build/reports/tests/test/index.html
```

## Ejecutar solo un test específico
```bash
./gradlew test --tests "UserServiceTest"
./gradlew test --tests "AuthServiceTest"
./gradlew test --tests "RoleServiceTest"
./gradlew test --tests "PermissionServiceTest"
```

## Ver salida detallada en consola
```bash
./gradlew test --info
```

## Tests unitarios disponibles (32 tests)

| Servicio | Archivo | Tests |
|---|---|---|
| AuthService | `AuthServiceTest.java` | login exitoso, credenciales inválidas |
| UserService | `UserServiceTest.java` | findAll, findById, create (éxito + 3 errores), update (éxito + not found), deactivate, reactivate |
| RoleService | `RoleServiceTest.java` | findAll, findById, create (éxito + duplicado), update, delete (éxito + not found), assignPermissions (éxito + permission faltante) |
| PermissionService | `PermissionServiceTest.java` | findAll, findById, create (éxito + duplicado), update (éxito + not found), delete (éxito + not found) |

## Notas
- Los tests usan JUnit 5 + Mockito (no necesitan base de datos)
- No requieren el servidor levantado
- Usan `@ExtendWith(MockitoExtension.class)` para mocking
