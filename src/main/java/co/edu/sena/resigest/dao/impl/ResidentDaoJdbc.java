package co.edu.sena.resigest.dao.impl;

import co.edu.sena.resigest.config.DatabaseConnection;
import co.edu.sena.resigest.dao.ResidentDao;
import co.edu.sena.resigest.exception.DataAccessException;
import co.edu.sena.resigest.model.Resident;
import co.edu.sena.resigest.model.ResidentStatus;
import co.edu.sena.resigest.model.ResidentType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacion JDBC de {@link ResidentDao}.
 * Todas las sentencias usan PreparedStatement para evitar inyeccion SQL,
 * y try-with-resources para cerrar Connection, PreparedStatement y ResultSet.
 *
 * @author Edwar Stiven Diaz Torres
 */
public class ResidentDaoJdbc implements ResidentDao {

    // ---------- Sentencias SQL (constantes en MAYUSCULAS_CON_GUION_BAJO) ----------

    private static final String SQL_INSERT =
            "INSERT INTO residente (tipo_documento, numero_documento, nombres, apellidos, telefono, "
            + "correo, tipo_residente, estado, id_apartamento) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_BASE =
            "SELECT r.id_residente, r.tipo_documento, r.numero_documento, r.nombres, r.apellidos, "
            + "r.telefono, r.correo, r.tipo_residente, r.estado, r.id_apartamento, r.fecha_registro, "
            + "t.numero_torre, a.numero AS numero_apartamento "
            + "FROM residente r "
            + "JOIN apartamento a ON a.id_apartamento = r.id_apartamento "
            + "JOIN torre t ON t.id_torre = a.id_torre ";

    private static final String SQL_FIND_ALL =
            SQL_SELECT_BASE + "ORDER BY t.numero_torre, a.numero, r.apellidos";

    private static final String SQL_FIND_BY_ID =
            SQL_SELECT_BASE + "WHERE r.id_residente = ?";

    private static final String SQL_FIND_BY_DOCUMENT =
            SQL_SELECT_BASE + "WHERE r.numero_documento = ?";

    private static final String SQL_FIND_BY_TOWER =
            SQL_SELECT_BASE + "WHERE t.numero_torre = ? ORDER BY a.numero, r.apellidos";

    private static final String SQL_UPDATE =
            "UPDATE residente SET tipo_documento = ?, numero_documento = ?, nombres = ?, apellidos = ?, "
            + "telefono = ?, correo = ?, tipo_residente = ?, estado = ?, id_apartamento = ? "
            + "WHERE id_residente = ?";

    private static final String SQL_DELETE =
            "DELETE FROM residente WHERE id_residente = ?";

    // ---------- INSERCION (Create) ----------

    @Override
    public int insert(Resident resident) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            fillStatement(statement, resident);
            statement.executeUpdate();

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    resident.setResidentId(newId);
                    return newId;
                }
            }
            throw new DataAccessException("MySQL no devolvio el id del nuevo residente", null);
        } catch (SQLException ex) {
            throw new DataAccessException("Error insertando el residente: " + ex.getMessage(), ex);
        }
    }

    // ---------- CONSULTA (Read) ----------

    @Override
    public List<Resident> findAll() {
        List<Resident> residents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ALL);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                residents.add(mapRow(resultSet));
            }
            return residents;
        } catch (SQLException ex) {
            throw new DataAccessException("Error consultando los residentes", ex);
        }
    }

    @Override
    public Optional<Resident> findById(int residentId) {
        return findOne(SQL_FIND_BY_ID, statement -> statement.setInt(1, residentId));
    }

    @Override
    public Optional<Resident> findByDocumentNumber(String documentNumber) {
        return findOne(SQL_FIND_BY_DOCUMENT, statement -> statement.setString(1, documentNumber));
    }

    @Override
    public List<Resident> findByTower(int towerNumber) {
        List<Resident> residents = new ArrayList<>();
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_BY_TOWER)) {

            statement.setInt(1, towerNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    residents.add(mapRow(resultSet));
                }
            }
            return residents;
        } catch (SQLException ex) {
            throw new DataAccessException("Error consultando los residentes de la torre", ex);
        }
    }

    // ---------- ACTUALIZACION (Update) ----------

    @Override
    public boolean update(Resident resident) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_UPDATE)) {

            fillStatement(statement, resident);
            statement.setInt(10, resident.getResidentId());
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error actualizando el residente: " + ex.getMessage(), ex);
        }
    }

    // ---------- ELIMINACION (Delete) ----------

    @Override
    public boolean delete(int residentId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_DELETE)) {

            statement.setInt(1, residentId);
            return statement.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DataAccessException("Error eliminando el residente: " + ex.getMessage(), ex);
        }
    }

    // ---------- Metodos auxiliares privados ----------

    /** Asigna los 9 parametros comunes de INSERT y UPDATE. */
    private void fillStatement(PreparedStatement statement, Resident resident) throws SQLException {
        statement.setString(1, resident.getDocumentType());
        statement.setString(2, resident.getDocumentNumber());
        statement.setString(3, resident.getFirstName());
        statement.setString(4, resident.getLastName());
        statement.setString(5, resident.getPhone());
        statement.setString(6, resident.getEmail());
        statement.setString(7, resident.getResidentType().name());
        statement.setString(8, resident.getStatus().name());
        statement.setInt(9, resident.getApartmentId());
    }

    /** Convierte una fila del ResultSet en un objeto Resident. */
    private Resident mapRow(ResultSet resultSet) throws SQLException {
        Resident resident = new Resident();
        resident.setResidentId(resultSet.getInt("id_residente"));
        resident.setDocumentType(resultSet.getString("tipo_documento"));
        resident.setDocumentNumber(resultSet.getString("numero_documento"));
        resident.setFirstName(resultSet.getString("nombres"));
        resident.setLastName(resultSet.getString("apellidos"));
        resident.setPhone(resultSet.getString("telefono"));
        resident.setEmail(resultSet.getString("correo"));
        resident.setResidentType(ResidentType.valueOf(resultSet.getString("tipo_residente")));
        resident.setStatus(ResidentStatus.valueOf(resultSet.getString("estado")));
        resident.setApartmentId(resultSet.getInt("id_apartamento"));
        Timestamp registeredAt = resultSet.getTimestamp("fecha_registro");
        if (registeredAt != null) {
            resident.setRegisteredAt(registeredAt.toLocalDateTime());
        }
        resident.setTowerNumber(resultSet.getInt("numero_torre"));
        resident.setApartmentNumber(resultSet.getString("numero_apartamento"));
        return resident;
    }

    /** Ejecuta una consulta que devuelve maximo un registro. */
    private Optional<Resident> findOne(String sql, ParameterSetter parameterSetter) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            parameterSetter.setParameters(statement);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error consultando el residente", ex);
        }
    }

    /** Interfaz funcional para asignar parametros a un PreparedStatement. */
    @FunctionalInterface
    private interface ParameterSetter {
        void setParameters(PreparedStatement statement) throws SQLException;
    }
}
