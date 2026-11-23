package co.edu.sena.resigest.service;

import co.edu.sena.resigest.dao.ApartmentDao;
import co.edu.sena.resigest.dao.ResidentDao;
import co.edu.sena.resigest.exception.ValidationException;
import co.edu.sena.resigest.model.Resident;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Logica de negocio del modulo de residentes.
 * Valida los datos antes de enviarlos al DAO (capa de acceso a datos).
 *
 * @author Edwar Stiven Diaz Torres
 */
public class ResidentService {

    private static final Set<String> VALID_DOCUMENT_TYPES = Set.of("CC", "CE", "TI", "PA");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^3\\d{9}$");
    private static final int MIN_TOWER = 1;
    private static final int MAX_TOWER = 17;

    private final ResidentDao residentDao;
    private final ApartmentDao apartmentDao;

    public ResidentService(ResidentDao residentDao, ApartmentDao apartmentDao) {
        this.residentDao = residentDao;
        this.apartmentDao = apartmentDao;
    }

    /** Registra un nuevo residente en el apartamento indicado. */
    public int registerResident(Resident resident, int towerNumber, String apartmentNumber) {
        validateResident(resident);
        if (residentDao.findByDocumentNumber(resident.getDocumentNumber()).isPresent()) {
            throw new ValidationException("Ya existe un residente con el documento " + resident.getDocumentNumber());
        }
        resident.setApartmentId(resolveApartmentId(towerNumber, apartmentNumber));
        return residentDao.insert(resident);
    }

    public List<Resident> listResidents() {
        return residentDao.findAll();
    }

    public List<Resident> listResidentsByTower(int towerNumber) {
        validateTower(towerNumber);
        return residentDao.findByTower(towerNumber);
    }

    public Optional<Resident> findByDocument(String documentNumber) {
        return residentDao.findByDocumentNumber(documentNumber.trim());
    }

    public Optional<Resident> findById(int residentId) {
        return residentDao.findById(residentId);
    }

    /** Actualiza un residente existente; puede cambiarlo de apartamento. */
    public void updateResident(Resident resident, int towerNumber, String apartmentNumber) {
        validateResident(resident);
        Optional<Resident> sameDocument = residentDao.findByDocumentNumber(resident.getDocumentNumber());
        if (sameDocument.isPresent() && sameDocument.get().getResidentId() != resident.getResidentId()) {
            throw new ValidationException("El documento " + resident.getDocumentNumber() + " ya pertenece a otro residente");
        }
        resident.setApartmentId(resolveApartmentId(towerNumber, apartmentNumber));
        if (!residentDao.update(resident)) {
            throw new ValidationException("No existe un residente con id " + resident.getResidentId());
        }
    }

    public void deleteResident(int residentId) {
        if (!residentDao.delete(residentId)) {
            throw new ValidationException("No existe un residente con id " + residentId);
        }
    }

    // ---------- Validaciones ----------

    private void validateResident(Resident resident) {
        requireText(resident.getFirstName(), "nombres");
        requireText(resident.getLastName(), "apellidos");
        requireText(resident.getDocumentNumber(), "numero de documento");
        if (resident.getDocumentType() == null || !VALID_DOCUMENT_TYPES.contains(resident.getDocumentType())) {
            throw new ValidationException("Tipo de documento invalido. Use: " + VALID_DOCUMENT_TYPES);
        }
        if (resident.getPhone() == null || !PHONE_PATTERN.matcher(resident.getPhone()).matches()) {
            throw new ValidationException("El telefono debe ser un celular de 10 digitos que empiece por 3");
        }
        String email = resident.getEmail();
        if (email != null && !email.isBlank() && !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("El correo electronico no tiene un formato valido");
        }
        if (resident.getResidentType() == null || resident.getStatus() == null) {
            throw new ValidationException("Debe indicar el tipo de residente y el estado");
        }
    }

    private int resolveApartmentId(int towerNumber, String apartmentNumber) {
        validateTower(towerNumber);
        return apartmentDao.findIdByTowerAndNumber(towerNumber, apartmentNumber)
                .orElseThrow(() -> new ValidationException(
                        "No existe el apartamento " + apartmentNumber + " en la torre " + towerNumber));
    }

    private void validateTower(int towerNumber) {
        if (towerNumber < MIN_TOWER || towerNumber > MAX_TOWER) {
            throw new ValidationException("La torre debe estar entre " + MIN_TOWER + " y " + MAX_TOWER);
        }
    }

    private void requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new ValidationException("El campo " + fieldName + " es obligatorio");
        }
    }
}
