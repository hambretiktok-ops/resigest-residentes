package co.edu.sena.resigest.app;

import co.edu.sena.resigest.dao.ApartmentDao;
import co.edu.sena.resigest.dao.ResidentDao;
import co.edu.sena.resigest.dao.impl.ApartmentDaoJdbc;
import co.edu.sena.resigest.dao.impl.ResidentDaoJdbc;
import co.edu.sena.resigest.service.ResidentService;
import co.edu.sena.resigest.view.ResidentConsoleView;

import java.util.Scanner;

/**
 * Punto de entrada del modulo de Gestion de Residentes de ResiGest.
 * Arma las capas: DAO (JDBC) -> Servicio (validaciones) -> Vista (consola).
 *
 * @author Edwar Stiven Diaz Torres
 */
public class Main {

    public static void main(String[] args) {
        ResidentDao residentDao = new ResidentDaoJdbc();
        ApartmentDao apartmentDao = new ApartmentDaoJdbc();
        ResidentService residentService = new ResidentService(residentDao, apartmentDao);

        try (Scanner scanner = new Scanner(System.in)) {
            ResidentConsoleView view = new ResidentConsoleView(residentService, scanner);
            view.start();
        }
    }
}
