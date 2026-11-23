# Pruebas del módulo de Gestión de Residentes – ResiGest

**Evidencia:** GA7-220501096-AA2-EV02 – Módulos de software codificados y probados
**Aprendiz:** Edwar Stiven Diaz Torres · **Ficha:** 3389005
**Instructor:** Manuel Dussan

Las pruebas se ejecutan desde la **terminal** con la clase `App.java`
(`src/main/java/co/edu/sena/resigest/app/App.java`). Esta clase usa el DAO y el
servicio del módulo (EV01) contra la base de datos real `resigest` y muestra
`[APROBADA]` o `[FALLIDA]` en cada verificación.

El residente de prueba (documento `9999999999`, torre 17, apto 404) se crea y se
elimina dentro de la misma ejecución, así que la base de datos queda igual que al inicio.

## Cómo ejecutar las pruebas

1. Iniciar **MySQL** en XAMPP.
2. Abrir la carpeta del proyecto en **Visual Studio Code**.
3. Abrir una terminal (**Terminal → New Terminal**) parada en la carpeta del proyecto (donde está `pom.xml`).
4. Ejecutar:

```bash
mvn clean compile exec:java "-Dexec.mainClass=co.edu.sena.resigest.app.App"
```

## Casos de prueba

| N.° | Operación | Prueba | Entrada | Resultado esperado |
|---|---|---|---|---|
| 1 | Conexión | Conexión JDBC a MySQL | `db.properties` | La conexión está activa |
| 2 | Consulta (SELECT) | Listar todos los residentes | — | Se muestra la lista de residentes |
| 3 | Inserción (INSERT) | Registrar residente de prueba | CC 9999999999, Torre 17, Apto 404 | MySQL genera un id y la tabla tiene un registro más |
| 4 | Consulta (SELECT … WHERE) | Buscar por documento y por torre | 9999999999 / Torre 17 | Se encuentra el residente con los datos enviados |
| 5 | Actualización (UPDATE) | Cambiar celular y estado | 3119998877 / INACTIVO | Los datos quedan actualizados en la base |
| 6.1 | Validación | Documento repetido | 9999999999 | Rechazado: "Ya existe un residente con el documento…" |
| 6.2 | Validación | Celular inválido | 12345 | Rechazado: "El telefono debe ser un celular de 10 digitos…" |
| 6.3 | Validación | Correo mal escrito | ana.correo.com | Rechazado: "El correo electronico no tiene un formato valido" |
| 6.4 | Validación | Torre inexistente | Torre 18 | Rechazado: "La torre debe estar entre 1 y 17" |
| 6.5 | Validación | Nombres vacíos | "" | Rechazado: "El campo nombres es obligatorio" |
| 7 | Eliminación (DELETE) | Eliminar residente de prueba | id generado | El residente ya no existe; un id inexistente se rechaza |
| 8 | Consulta (SELECT) | Consulta final | — | La tabla queda con la misma cantidad del inicio |

## Resultado

Al final de la ejecución la terminal muestra el resumen:

```
RESUMEN DE PRUEBAS
Pruebas aprobadas: 17
Pruebas fallidas:  0
Resultado: EL MODULO PASO TODAS LAS PRUEBAS
```
