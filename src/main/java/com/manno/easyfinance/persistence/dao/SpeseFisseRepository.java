package com.manno.easyfinance.persistence.dao;

import com.manno.easyfinance.persistence.model.SpeseFisse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class SpeseFisseRepository implements Repository<SpeseFisse,String[]>{

    private final PGSimpleDataSource dataSource;

    public SpeseFisseRepository(PGSimpleDataSource dataSource) throws SQLException {
        this.dataSource=dataSource;
        checkTable();
    }

    private void checkTable() throws SQLException {
        String sql = "SELECT * FROM SpeseFisse LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("table Account doesn't exists or not populated");
        }
    }

    @Override
    public Optional<SpeseFisse> findById(String[] strings) {
        String sql = "SELECT * FROM SpeseFisse WHERE (nomeConto,descrizione)=(?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, strings[0]);
            statement.setString(2, strings[1]);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(new SpeseFisse(rs.getString("nomeConto"), rs.getString("descrizione"), rs.getDouble("amount")));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Iterable<SpeseFisse> findAll() {
        String sql = "SELECT * FROM SpeseFisse";
        List<SpeseFisse> spesefisseList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                spesefisseList.add(new SpeseFisse(rs.getString("nomeConto"), rs.getString("descrizione"), rs.getDouble("amount")));
            }
            return spesefisseList;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SpeseFisse save(SpeseFisse entity) {
        if (Objects.isNull(entity.getChiave())) {
            return insert(entity);
        }

        Optional<SpeseFisse> spesa = findById(entity.getChiave());
        if (spesa.isEmpty()) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private SpeseFisse insert(SpeseFisse entity) {
        String sql = "INSERT INTO SpeseFisse (nomeConto, descrizione, amount) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getNomeConto());
            statement.setString(2, entity.getDescrizione());
            statement.setDouble(3, entity.getAmount());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private SpeseFisse update(SpeseFisse entity) {
        String sql = "UPDATE SpeseFisse SET amount=? WHERE nomeConto=? AND descrizione=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, entity.getAmount());
            statement.setString(2, entity.getNomeConto());
            statement.setString(3, entity.getDescrizione());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(SpeseFisse entity) {
        deleteById(entity.getChiave());
    }

    @Override
    public void deleteById(String[] strings) {
        String sql = "DELETE FROM SpeseFisse WHERE nomeConto=? and descrizione=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, strings[0]);
            statement.setString(2, strings[1]);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM SpeseFisse";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ObservableList<SpeseFisse> findBynomeConto(String nomeConto){
        String sql = "SELECT * FROM SpeseFisse WHERE nomeConto=?";
        ObservableList<SpeseFisse> ret= FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomeConto);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret.add( new SpeseFisse(nomeConto, rs.getString("descrizione"), rs.getDouble("amount")));
            }
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
