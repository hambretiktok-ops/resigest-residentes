package co.edu.sena.resigest.app;

import co.edu.sena.resigest.config.DatabaseConnection;
import co.edu.sena.resigest.dao.ApartmentDao;
import co.edu.sena.resigest.dao.ResidentDao;
import co.edu.sena.resigest.dao.impl.ApartmentDaoJdbc;
import co.edu.sena.resigest.dao.impl.ResidentDaoJdbc;
import co.edu.sena.resigest.exception.ValidationException;
import co.edu.sena.resigest.model.Resident;
import co.edu.sena.resigest.model.ResidentStatus;
import co.edu.sena.resigest.model.ResidentType;
import co.edu.sena.resigest.service.ResidentService;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Pruebas del modulo de Gestion de Residentes ejecutadas desde la terminal.
 * Evidencia: GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 *
 * Recorre el CRUD completo (consulta, insercion, actualizacion y eliminacion)
 * contra la base de datos real y muestra si cada prueba fue APROBADA o FALLIDA.
 * El residente de prueba se elimina al final, asi la base queda como estaba.
 *
 * Ejecutar desde la carpeta del proyecto:
 *   mvn clean compile exec:java "-Dexec.mainClass=co.edu.sena.resigest.app.App"
 *
 * @author Edwar Stiven Diaz Torres
 */
public class App {

    // Datos del residente de prueba (torre 17, apartamento 404)
    private static final String TEST_DOCUMENT = "9999999999";
    private static final int TEST_TOWER = 17;
    private static final String TEST_APARTMENT = "404";
    private static final String UPDATED_PHONE = "3119998877";

    private static int passedTests = 0;
    private static int failedTests = 0;

    public static void main(String[] args) {
        ResidentDao residentDao = new ResidentDaoJdbc();
        ApartmentDao apartmentDao = new ApartmentDaoJdbc();
        ResidentService residentService = new ResidentService(residentDao, apartmentDao);

        printTitle("PRUEBAS DEL MODULO DE GESTION DE RESIDENTES - RESIGEST");

        // Si quedo un residente de prueba de una ejecucion anterior, se borra
        residentService.findByDocument(TEST_DOCUMENT)
                .ifPresent(old -> residentService.deleteResident(old.getResidentId()));

        // ---------- Prueba 1: conexion ----------
        printTitle("Prueba 1: Conexion a la base de datos (JDBC)");
        try (Connection connection = DatabaseConnection.getConnection()) {
            check("La conexion con MySQL esta activa", connection.isValid(2));
        } catch (Exception ex) {
            check("La conexion con MySQL esta activa (" + ex.getMessage() + ")", false);
            printSummary();
            return;
        }

        // ---------- Prueba 2: consultar todos ----------
        printTitle("Prueba 2: Consultar residentes (SELECT)");
        List<Resident> initialResidents = residentService.listResidents();
        printResidents(initialResidents);
        check("La consulta devuelve la lista de residentes", initialResidents != null);
        int initialCount = initialResidents.size();

        // ---------- Prueba 3: insertar ----------
        printTitle("Prueba 3: Insertar residente (INSERT)");
        Resident newResident = new Resident("CC", TEST_DOCUMENT, "Residente", "De Prueba",
                "3001234567", "prueba@resigest.com", ResidentType.ARRENDATARIO, ResidentStatus.ACTIVO);
        int newId = residentService.registerResident(newResident, TEST_TOWER, TEST_APARTMENT);
        System.out.println("Residente insertado con id " + newId);
        check("MySQL genero un id para el nuevo residente", newId > 0);
        check("Ahora hay un residente mas en la tabla",
                residentService.listResidents().size() == initialCount + 1);

        // ---------- Prueba 4: consultar por documento y por torre ----------
        printTitle("Prueba 4: Consultar por documento y por torre (SELECT ... WHERE)");
        Optional<Resident> found = residentService.findByDocument(TEST_DOCUMENT);
        found.ifPresent(resident -> printResidents(List.of(resident)));
        check("Se encuentra el residente por su documento", found.isPresent());
        check("El nombre guardado es el que se envio",
                found.isPresent() && found.get().getFullName().equals("Residente De Prueba"));
        boolean inTower = residentService.listResidentsByTower(TEST_TOWER).stream()
                .anyMatch(resident -> resident.getResidentId() == newId);
        check("Aparece en la consulta de la torre " + TEST_TOWER, inTower);

        // ---------- Prueba 5: actualizar ----------
        printTitle("Prueba 5: Actualizar residente (UPDATE)");
        Resident toUpdate = residentService.findById(newId).orElseThrow();
        toUpdate.setPhone(UPDATED_PHONE);
        toUpdate.setStatus(ResidentStatus.INACTIVO);
        residentService.updateResident(toUpdate, TEST_TOWER, TEST_APARTMENT);
        Resident updated = residentService.findById(newId).orElseThrow();
        printResidents(List.of(updated));
        check("El telefono se actualizo a " + UPDATED_PHONE, UPDATED_PHONE.equals(updated.getPhone()));
        check("El estado se actualizo a INACTIVO", updated.getStatus() == ResidentStatus.INACTIVO);

        // ---------- Prueba 6: validaciones ----------
        printTitle("Prueba 6: Validaciones de datos");
        expectRejection("Rechaza un documento repetido", () -> residentService.registerResident(
                new Resident("CC", TEST_DOCUMENT, "Otra", "Persona", "3001112233", null,
                        ResidentType.PROPIETARIO, ResidentStatus.ACTIVO), 1, "101"));
        expectRejection("Rechaza un celular invalido (12345)", () -> residentService.registerResident(
                new Resident("CC", "1111111111", "Ana", "Ruiz", "12345", null,
                        ResidentType.PROPIETARIO, ResidentStatus.ACTIVO), 1, "101"));
        expectRejection("Rechaza un correo mal escrito", () -> residentService.registerResident(
                new Resident("CC", "1111111111", "Ana", "Ruiz", "3001112233", "ana.correo.com",
                        ResidentType.PROPIETARIO, ResidentStatus.ACTIVO), 1, "101"));
        expectRejection("Rechaza una torre que no existe (18)", () -> residentService.registerResident(
                new Resident("CC", "1111111111", "Ana", "Ruiz", "3001112233", null,
                        ResidentType.PROPIETARIO, ResidentStatus.ACTIVO), 18, "101"));
        expectRejection("Rechaza nombres vacios", () -> residentService.registerResident(
                new Resident("CC", "1111111111", "", "Ruiz", "3001112233", null,
                        ResidentType.PROPIETARIO, ResidentStatus.ACTIVO), 1, "101"));

        // ---------- Prueba 7: eliminar ----------
        printTitle("Prueba 7: Eliminar residente (DELETE)");
        residentService.deleteResident(newId);
        System.out.println("Residente con id " + newId + " eliminado");
        check("El residente ya no existe en la base de datos", residentService.findById(newId).isEmpty());
        expectRejection("No deja eliminar un id que no existe", () -> residentService.deleteResident(newId));

        // ---------- Prueba 8: consulta final ----------
        printTitle("Prueba 8: Consulta final (la tabla queda como al inicio)");
        List<Resident> finalResidents = residentService.listResidents();
        printResidents(finalResidents);
        check("La tabla tiene la misma cantidad de residentes del inicio",
                finalResidents.size() == initialCount);

        printSummary();
    }

    // ---------- Metodos auxiliares ----------

    /** Registra el resultado de una prueba como APROBADA o FALLIDA. */
    private static void check(String description, boolean condition) {
        if (condition) {
            passedTests++;
            System.out.println("  [APROBADA] " + description);
        } else {
            failedTests++;
            System.out.println("  [FALLIDA]  " + description);
        }
    }

    /** Verifica que una operacion con datos invalidos sea rechazada por el servicio. */
    private static void expectRejection(String description, Runnable operation) {
        try {
            operation.run();
            check(description + " -> no lanzo error", false);
        } catch (ValidationException ex) {
            check(description + " -> \"" + ex.getMessage() + "\"", true);
        }
    }

    private static void printResidents(List<Resident> residents) {
        System.out.println("Lista de residentes:");
        for (Resident resident : residents) {
            System.out.println("  " + resident.getResidentId() + " - " + resident.getFullName()
                    + " - " + resident.getDocumentType() + " " + resident.getDocumentNumber()
                    + " - Torre " + resident.getTowerNumber() + " Apto " + resident.getApartmentNumber()
                    + " - " + resident.getPhone() + " - " + resident.getStatus());
        }
    }

    private static void printTitle(String title) {
        System.out.println();
        System.out.println("==================================================================");
        System.out.println(title);
        System.out.println("==================================================================");
    }

    private static void printSummary() {
        printTitle("RESUMEN DE PRUEBAS");
        System.out.println("Pruebas aprobadas: " + passedTests);
        System.out.println("Pruebas fallidas:  " + failedTests);
        System.out.println(failedTests == 0
                ? "Resultado: EL MODULO PASO TODAS LAS PRUEBAS"
                : "Resultado: HAY PRUEBAS FALLIDAS, revise los mensajes anteriores");
    }
}
