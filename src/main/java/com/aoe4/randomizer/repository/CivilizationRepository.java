package com.aoe4.randomizer.repository;

import com.aoe4.randomizer.model.Civilization;
import com.aoe4.randomizer.persistence.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CivilizationRepository {
    private final DatabaseManager databaseManager;

    public CivilizationRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public List<Civilization> findAllByOrderByDlcAscNameAsc() {
        return queryMany("SELECT id, name, dlc, icon_path, enabled FROM civilization ORDER BY dlc ASC, name ASC");
    }

    public List<Civilization> findByEnabledTrue() {
        return queryMany("SELECT id, name, dlc, icon_path, enabled FROM civilization WHERE enabled = TRUE ORDER BY dlc ASC, name ASC");
    }

    public List<Civilization> findByDlc(String dlc) {
        String sql = "SELECT id, name, dlc, icon_path, enabled FROM civilization WHERE dlc = ? ORDER BY name ASC";
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, dlc);
            return readMany(statement.executeQuery());
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query civilizations for DLC: " + dlc, ex);
        }
    }

    public Optional<Civilization> findById(long id) {
        String sql = "SELECT id, name, dlc, icon_path, enabled FROM civilization WHERE id = ?";
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRow(resultSet));
                }
                return Optional.empty();
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query civilization: " + id, ex);
        }
    }

    public Civilization save(Civilization civilization) {
        String sql = "UPDATE civilization SET name = ?, dlc = ?, icon_path = ?, enabled = ? WHERE id = ?";
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            bindCivilization(statement, civilization);
            statement.setLong(5, civilization.getId());
            statement.executeUpdate();
            return civilization;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to save civilization: " + civilization.getName(), ex);
        }
    }

    public List<Civilization> saveAll(List<Civilization> civilizations) {
        String sql = "UPDATE civilization SET name = ?, dlc = ?, icon_path = ?, enabled = ? WHERE id = ?";
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            connection.setAutoCommit(false);
            for (Civilization civilization : civilizations) {
                bindCivilization(statement, civilization);
                statement.setLong(5, civilization.getId());
                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();
            connection.setAutoCommit(true);
            return civilizations;
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to save civilizations", ex);
        }
    }

    private List<Civilization> queryMany(String sql) {
        try (Connection connection = databaseManager.openConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            return readMany(resultSet);
        } catch (SQLException ex) {
            throw new IllegalStateException("Failed to query civilizations", ex);
        }
    }

    private List<Civilization> readMany(ResultSet resultSet) throws SQLException {
        List<Civilization> civilizations = new ArrayList<>();
        while (resultSet.next()) {
            civilizations.add(mapRow(resultSet));
        }
        return civilizations;
    }

    private Civilization mapRow(ResultSet resultSet) throws SQLException {
        Civilization civilization = new Civilization();
        civilization.setId(resultSet.getLong("id"));
        civilization.setName(resultSet.getString("name"));
        civilization.setDlc(resultSet.getString("dlc"));
        civilization.setIconPath(resultSet.getString("icon_path"));
        civilization.setEnabled(resultSet.getBoolean("enabled"));
        return civilization;
    }

    private void bindCivilization(PreparedStatement statement, Civilization civilization) throws SQLException {
        statement.setString(1, civilization.getName());
        statement.setString(2, civilization.getDlc());
        statement.setString(3, civilization.getIconPath());
        statement.setBoolean(4, civilization.isEnabled());
    }
}
