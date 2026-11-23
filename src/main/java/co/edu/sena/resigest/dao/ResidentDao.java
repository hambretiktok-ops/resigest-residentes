package co.edu.sena.resigest.dao;

import co.edu.sena.resigest.model.Resident;

import java.util.List;
import java.util.Optional;

/**
 * Contrato de acceso a datos para la tabla RESIDENTE.
 * Define las operaciones CRUD: insertar, consultar, actualizar y eliminar.
 *
 * @author Edwar Stiven Diaz Torres
 */
public interface ResidentDao {

    /** Inserta un residente y devuelve el id generado por MySQL. */
    int insert(Resident resident);

    /** Consulta todos los residentes ordenados por torre y apartamento. */
    List<Resident> findAll();

    /** Consulta un residente por su id. */
    Optional<Resident> findById(int residentId);

    /** Consulta un residente por su numero de documento. */
    Optional<Resident> findByDocumentNumber(String documentNumber);

    /** Consulta los residentes de una torre. */
    List<Resident> findByTower(int towerNumber);

    /** Actualiza los datos de un residente. Devuelve true si se modifico. */
    boolean update(Resident resident);

    /** Elimina un residente por su id. Devuelve true si se elimino. */
    boolean delete(int residentId);
}
