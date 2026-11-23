package co.edu.sena.resigest.dao;

import java.util.Optional;

/**
 * Contrato de acceso a datos para la tabla APARTAMENTO.
 *
 * @author Edwar Stiven Diaz Torres
 */
public interface ApartmentDao {

    /**
     * Busca el id de un apartamento a partir del numero de torre y el numero del apartamento.
     * Ejemplo: torre 3, apartamento "204".
     */
    Optional<Integer> findIdByTowerAndNumber(int towerNumber, String apartmentNumber);
}
