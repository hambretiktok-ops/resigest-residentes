# ResiGest – Módulo de Gestión de Residentes

**Evidencia:** GA7-220501096-AA2-EV01 – Codificación de módulos del software
**Programa:** Tecnología en Análisis y Desarrollo de Software (ADSO) – SENA
**Aprendiz:** Edwar Stiven Diaz Torres · **Ficha:** 3389005
**Instructor:** Manuel Dussan

Módulo del sistema ResiGest (administración de propiedad horizontal: 17 torres, 272 apartamentos) que permite **registrar, consultar, actualizar y eliminar residentes**, con conexión a MySQL mediante **JDBC**.

---

## 1. Tecnologías

| Elemento | Herramienta |
|---|---|
| Lenguaje | Java 17 o superior |
| Base de datos | MySQL 8 (MySQL Workbench) |
| Conexión | JDBC – driver `mysql-connector-j` 8.4.0 |
| Gestión del proyecto | Apache Maven (`pom.xml`) |
| Versionamiento | Git + GitHub |
| IDE sugerido | NetBeans, IntelliJ IDEA o VS Code |

## 2. Estructura del proyecto (arquitectura por capas)

```
resigest-residentes/
├── pom.xml                          -> dependencias (driver JDBC)
├── database/
│   └── resigest_residentes.sql      -> crea la BD, tablas y datos de prueba
└── src/main/
    ├── resources/db.properties      -> URL, usuario y clave de MySQL
    └── java/co/edu/sena/resigest/
        ├── app/Main.java            -> punto de entrada
        ├── config/DatabaseConnection.java   -> abre la conexión JDBC
        ├── model/                   -> Resident, ResidentType, ResidentStatus
        ├── dao/                     -> interfaces ResidentDao, ApartmentDao
        ├── dao/impl/                -> ResidentDaoJdbc, ApartmentDaoJdbc (SQL + JDBC)
        ├── service/ResidentService.java     -> validaciones / reglas de negocio
        ├── view/ResidentConsoleView.java    -> menú por consola
        └── exception/               -> DataAccessException, ValidationException
```

Flujo: **Vista → Servicio → DAO → JDBC → MySQL**

## 3. Funcionalidades CRUD

| Operación | Método DAO | Sentencia SQL |
|---|---|---|
| Inserción | `insert(Resident)` | `INSERT INTO residente ...` |
| Consulta | `findAll()`, `findById()`, `findByDocumentNumber()`, `findByTower()` | `SELECT ... JOIN apartamento JOIN torre` |
| Actualización | `update(Resident)` | `UPDATE residente SET ... WHERE id_residente = ?` |
| Eliminación | `delete(int)` | `DELETE FROM residente WHERE id_residente = ?` |

Todas las sentencias usan `PreparedStatement` (evita inyección SQL) y `try-with-resources` (cierra automáticamente la conexión).

Validaciones del servicio: campos obligatorios, tipo de documento (CC/CE/TI/PA), celular de 10 dígitos que empieza por 3, formato de correo, documento no repetido, torre entre 1 y 17 y apartamento existente.

## 4. Estándares de codificación aplicados

| Elemento | Convención | Ejemplos |
|---|---|---|
| Paquetes | minúsculas, dominio invertido | `co.edu.sena.resigest.dao.impl` |
| Clases e interfaces | PascalCase, sustantivos | `Resident`, `ResidentDao`, `ResidentDaoJdbc` |
| Métodos | camelCase, inician con verbo | `insert()`, `findByTower()`, `registerResident()` |
| Variables y atributos | camelCase, descriptivos | `documentNumber`, `towerNumber`, `resultSet` |
| Constantes | MAYÚSCULAS_CON_GUION_BAJO | `SQL_INSERT`, `MAX_TOWER`, `EMAIL_PATTERN` |
| Enumeraciones | PascalCase, valores en mayúscula | `ResidentStatus.ACTIVO` |
| Tablas/columnas SQL | snake_case | `residente`, `numero_documento` |
| Identificadores | en inglés (código) | según el estándar definido en GA7-220501096-AA1-EV02 |

## 5. Cómo ejecutar

1. Abrir **MySQL Workbench** y ejecutar el script `database/resigest_residentes.sql`.
2. Editar `src/main/resources/db.properties` con el usuario y la clave de su MySQL.
3. Abrir la carpeta del proyecto en NetBeans / IntelliJ / VS Code (se reconoce como proyecto Maven y descarga el driver solo).
4. Ejecutar la clase `co.edu.sena.resigest.app.Main`.
   Por terminal: `mvn compile exec:java`

## 6. Control de versiones (Git + GitHub)

Estrategia de ramas definida en GA7-220501096-AA1-EV01: `main`, `develop` y `feature/*`.

```bash
git init
git add .
git commit -m "chore: estructura inicial del proyecto Maven"
git branch -M main
git checkout -b develop
git checkout -b feature/modulo-residentes
# ... trabajo del módulo ...
git add .
git commit -m "feat(residentes): CRUD de residentes con JDBC"
git checkout develop
git merge feature/modulo-residentes
git checkout main
git merge develop
git tag v1.0.0
git remote add origin https://github.com/USUARIO/resigest-residentes.git
git push -u origin main develop feature/modulo-residentes --tags
```
