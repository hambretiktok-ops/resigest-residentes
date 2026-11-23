package co.edu.sena.resigest.dao.impl;

import co.edu.sena.resigest.config.DatabaseConnection;
import co.edu.sena.resigest.dao.ApartmentDao;
import co.edu.sena.resigest.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Implementacion JDBC de {@link ApartmentDao}.
 *
 * @author Edwar Stiven Diaz Torres
 */
public class ApartmentDaoJdbc implements ApartmentDao {

    private static final String SQL_FIND_ID_BY_TOWER_AND_NUMBER =
            "SELECT a.id_apartamento FROM apartamento a "
            + "JOIN torre t ON t.id_torre = a.id_torre "
            + "WHERE t.numero_torre = ? AND a.numero = ?";

    @Override
    public Optional<Integer> findIdByTowerAndNumber(int towerNumber, String apartmentNumber) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_FIND_ID_BY_TOWER_AND_NUMBER)) {

            statement.setInt(1, towerNumber);
            statement.setString(2, apartmentNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(resultSet.getInt("id_apartamento"));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error consultando el apartamento", ex);
        }
    }
}
