package co.edu.sena.resigest.model;

import java.time.LocalDateTime;

/**
 * Clase de modelo (entidad) que representa la tabla RESIDENTE.
 * Aplica encapsulamiento: atributos privados con getters y setters.
 *
 * @author Edwar Stiven Diaz Torres
 */
public class Resident {

    private int residentId;
    private String documentType;
    private String documentNumber;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private ResidentType residentType;
    private ResidentStatus status;
    private int apartmentId;
    private LocalDateTime registeredAt;

    // Datos de solo lectura que vienen del JOIN con TORRE y APARTAMENTO
    private int towerNumber;
    private String apartmentNumber;

    public Resident() {
    }

    public Resident(String documentType, String documentNumber, String firstName, String lastName,
                    String phone, String email, ResidentType residentType, ResidentStatus status) {
        this.documentType = documentType;
        this.documentNumber = documentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.residentType = residentType;
        this.status = status;
    }

    public int getResidentId() {
        return residentId;
    }

    public void setResidentId(int residentId) {
        this.residentId = residentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ResidentType getResidentType() {
        return residentType;
    }

    public void setResidentType(ResidentType residentType) {
        this.residentType = residentType;
    }

    public ResidentStatus getStatus() {
        return status;
    }

    public void setStatus(ResidentStatus status) {
        this.status = status;
    }

    public int getApartmentId() {
        return apartmentId;
    }

    public void setApartmentId(int apartmentId) {
        this.apartmentId = apartmentId;
    }

    public LocalDateTime getRegisteredAt() {
        return registeredAt;
    }

    public void setRegisteredAt(LocalDateTime registeredAt) {
        this.registeredAt = registeredAt;
    }

    public int getTowerNumber() {
        return towerNumber;
    }

    public void setTowerNumber(int towerNumber) {
        this.towerNumber = towerNumber;
    }

    public String getApartmentNumber() {
        return apartmentNumber;
    }

    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return "Resident{id=" + residentId + ", documento=" + documentType + " " + documentNumber
                + ", nombre=" + getFullName() + ", torre=" + towerNumber + ", apto=" + apartmentNumber
                + ", tipo=" + residentType + ", estado=" + status + "}";
    }
}
