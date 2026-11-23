package co.edu.sena.resigest.view;

import co.edu.sena.resigest.exception.DataAccessException;
import co.edu.sena.resigest.exception.ValidationException;
import co.edu.sena.resigest.model.Resident;
import co.edu.sena.resigest.model.ResidentStatus;
import co.edu.sena.resigest.model.ResidentType;
import co.edu.sena.resigest.service.ResidentService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * Vista por consola del modulo de Gestion de Residentes.
 * Muestra el menu CRUD y pide los datos al usuario.
 *
 * @author Edwar Stiven Diaz Torres
 */
public class ResidentConsoleView {

    private static final String TABLE_FORMAT = "%-4s %-6s %-12s %-32s %-6s %-5s %-13s %-9s%n";

    private final ResidentService residentService;
    private final Scanner scanner;

    public ResidentConsoleView(ResidentService residentService, Scanner scanner) {
        this.residentService = residentService;
        this.scanner = scanner;
    }

    public void start() {
        int option;
        do {
            showMenu();
            option = readInt("Seleccione una opcion: ");
            try {
                handleOption(option);
            } catch (ValidationException ex) {
                System.out.println("[!] " + ex.getMessage());
            } catch (DataAccessException ex) {
                System.out.println("[ERROR BD] " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                System.out.println("[!] Valor no valido. Revise lo que escribio e intente de nuevo.");
            }
        } while (option != 0);
        System.out.println("Hasta pronto.");
    }

    private void showMenu() {
        System.out.println();
        System.out.println("=============================================");
        System.out.println("   RESIGEST - MODULO GESTION DE RESIDENTES");
        System.out.println("=============================================");
        System.out.println("1. Registrar residente        (Insertar)");
        System.out.println("2. Listar todos los residentes (Consultar)");
        System.out.println("3. Listar residentes por torre (Consultar)");
        System.out.println("4. Buscar por documento        (Consultar)");
        System.out.println("5. Actualizar residente        (Actualizar)");
        System.out.println("6. Eliminar residente          (Eliminar)");
        System.out.println("0. Salir");
    }

    private void handleOption(int option) {
        switch (option) {
            case 1 -> registerResident();
            case 2 -> printResidents(residentService.listResidents());
            case 3 -> printResidents(residentService.listResidentsByTower(readInt("Numero de torre (1-17): ")));
            case 4 -> searchByDocument();
            case 5 -> updateResident();
            case 6 -> deleteResident();
            case 0 -> { }
            default -> System.out.println("Opcion no valida.");
        }
    }

    // ---------- Insertar ----------

    private void registerResident() {
        System.out.println("\n--- Registrar residente ---");
        Resident resident = new Resident();
        resident.setDocumentType(readText("Tipo de documento (CC/CE/TI/PA): ").toUpperCase());
        resident.setDocumentNumber(readText("Numero de documento: "));
        resident.setFirstName(readText("Nombres: "));
        resident.setLastName(readText("Apellidos: "));
        resident.setPhone(readText("Celular: "));
        resident.setEmail(readText("Correo (opcional): "));
        resident.setResidentType(readResidentType());
        resident.setStatus(ResidentStatus.ACTIVO);
        int towerNumber = readInt("Torre (1-17): ");
        String apartmentNumber = readText("Apartamento (ej. 101, 204, 403): ");

        int newId = residentService.registerResident(resident, towerNumber, apartmentNumber);
        System.out.println("Residente registrado correctamente con id " + newId);
    }

    // ---------- Consultar ----------

    private void searchByDocument() {
        String documentNumber = readText("Numero de documento: ");
        Optional<Resident> resident = residentService.findByDocument(documentNumber);
        if (resident.isPresent()) {
            printResidents(List.of(resident.get()));
        } else {
            System.out.println("No se encontro un residente con ese documento.");
        }
    }

    private void printResidents(List<Resident> residents) {
        if (residents.isEmpty()) {
            System.out.println("No hay residentes para mostrar.");
            return;
        }
        System.out.println();
        System.out.printf(TABLE_FORMAT, "ID", "TIPO", "DOCUMENTO", "NOMBRE", "TORRE", "APTO", "TIPO RES.", "ESTADO");
        System.out.println("-".repeat(96));
        for (Resident resident : residents) {
            System.out.printf(TABLE_FORMAT,
                    resident.getResidentId(),
                    resident.getDocumentType(),
                    resident.getDocumentNumber(),
                    resident.getFullName(),
                    resident.getTowerNumber(),
                    resident.getApartmentNumber(),
                    resident.getResidentType(),
                    resident.getStatus());
        }
        System.out.println("Total: " + residents.size() + " residente(s)");
    }

    // ---------- Actualizar ----------

    private void updateResident() {
        System.out.println("\n--- Actualizar residente ---");
        int residentId = readInt("Id del residente a actualizar: ");
        Resident resident = residentService.findById(residentId)
                .orElseThrow(() -> new ValidationException("No existe un residente con id " + residentId));

        System.out.println("Deje el campo vacio y presione Enter para conservar el valor actual.");
        resident.setDocumentType(readOrKeep("Tipo de documento", resident.getDocumentType()).toUpperCase());
        resident.setDocumentNumber(readOrKeep("Numero de documento", resident.getDocumentNumber()));
        resident.setFirstName(readOrKeep("Nombres", resident.getFirstName()));
        resident.setLastName(readOrKeep("Apellidos", resident.getLastName()));
        resident.setPhone(readOrKeep("Celular", resident.getPhone()));
        resident.setEmail(readOrKeep("Correo", resident.getEmail()));
        resident.setResidentType(ResidentType.valueOf(
                readOrKeep("Tipo (PROPIETARIO/ARRENDATARIO)", resident.getResidentType().name()).toUpperCase()));
        resident.setStatus(ResidentStatus.valueOf(
                readOrKeep("Estado (ACTIVO/INACTIVO)", resident.getStatus().name()).toUpperCase()));
        int towerNumber = Integer.parseInt(readOrKeep("Torre", String.valueOf(resident.getTowerNumber())));
        String apartmentNumber = readOrKeep("Apartamento", resident.getApartmentNumber());

        residentService.updateResident(resident, towerNumber, apartmentNumber);
        System.out.println("Residente actualizado correctamente.");
    }

    // ---------- Eliminar ----------

    private void deleteResident() {
        System.out.println("\n--- Eliminar residente ---");
        int residentId = readInt("Id del residente a eliminar: ");
        Resident resident = residentService.findById(residentId)
                .orElseThrow(() -> new ValidationException("No existe un residente con id " + residentId));

        String confirmation = readText("Seguro que desea eliminar a " + resident.getFullName() + "? (S/N): ");
        if (confirmation.equalsIgnoreCase("S")) {
            residentService.deleteResident(residentId);
            System.out.println("Residente eliminado correctamente.");
        } else {
            System.out.println("Operacion cancelada.");
        }
    }

    // ---------- Lectura de datos por teclado ----------

    private String readText(String message) {
        System.out.print(message);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "0";
    }

    private int readInt(String message) {
        while (true) {
            String value = readText(message);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Debe ingresar un numero.");
            }
        }
    }

    private String readOrKeep(String label, String currentValue) {
        String value = readText(label + " [" + (currentValue == null ? "" : currentValue) + "]: ");
        return value.isEmpty() ? currentValue : value;
    }

    private ResidentType readResidentType() {
        while (true) {
            String value = readText("Tipo de residente (1 = Propietario, 2 = Arrendatario): ");
            if (value.equals("1")) {
                return ResidentType.PROPIETARIO;
            }
            if (value.equals("2")) {
                return ResidentType.ARRENDATARIO;
            }
            System.out.println("Opcion no valida.");
        }
    }
}
