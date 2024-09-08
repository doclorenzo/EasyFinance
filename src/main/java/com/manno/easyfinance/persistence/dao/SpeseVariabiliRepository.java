package com.manno.easyfinance.persistence.dao;

import com.manno.easyfinance.persistence.model.SpeseVariabili;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.*;
import java.util.*;

public class SpeseVariabiliRepository implements Repository<SpeseVariabili, AbstractMap.SimpleEntry<String,Integer>> {

    private final PGSimpleDataSource dataSource;

    public SpeseVariabiliRepository(PGSimpleDataSource dataSource) throws SQLException {
        this.dataSource=dataSource;
        checkTable();
    }

    private void checkTable() throws SQLException {
        String sql = "SELECT * FROM SpeseVariabili LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
             statement.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("table SpeseVariabili doesn't exists or not populated");
        }
    }

    @Override
    public Optional<SpeseVariabili> findById(AbstractMap.SimpleEntry<String, Integer> stringIntegerSimpleEntry) {
        String sql = "SELECT * FROM SpeseVariabili WHERE (nomeConto,id)=(?,?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, stringIntegerSimpleEntry.getKey());
            statement.setInt(2, stringIntegerSimpleEntry.getValue());
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(new SpeseVariabili(rs.getString("nomeConto"),  rs.getDouble("amount"), rs.getDate("gg"),rs.getString("descrizione"), rs.getInt("id")));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Iterable<SpeseVariabili> findAll() {
        String sql = "SELECT * FROM SpeseVariabili";
        List<SpeseVariabili> spesefisseList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                spesefisseList.add(new SpeseVariabili(rs.getString("nomeConto"),  rs.getDouble("amount"), rs.getDate("gg"),rs.getString("descrizione"), rs.getInt("id")));
            }
            return spesefisseList;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public SpeseVariabili save(SpeseVariabili entity) {
        if (Objects.isNull(entity.getChiave())) {
            return insert(entity);
        }

        Optional<SpeseVariabili> spesa = findById(entity.getChiave());
        if (spesa.isEmpty()) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private SpeseVariabili insert(SpeseVariabili entity) {
        String sql = "INSERT INTO SpeseVariabili (nomeConto, descrizione, amount, gg) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getNomeConto());
            statement.setString(2, entity.getDescrizione());
            statement.setDouble(3, entity.getAmount());
            statement.setDate(4, entity.getData());
            int affected=statement.executeUpdate();
            if(affected>0){
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int idGenerato = generatedKeys.getInt(1); // Recuperiamo l'ID auto-incrementato
                        entity.setId(idGenerato); // Impostiamo l'ID nel modello Utente
                        System.out.println("Utente inserito con ID: " + idGenerato);
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private SpeseVariabili update(SpeseVariabili entity) {
        String sql = "UPDATE SpeseVariabili SET amount=?, gg=? , descrizione=? WHERE nomeConto=? AND id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, entity.getAmount());
            statement.setDate(2, entity.getData());
            statement.setString(3, entity.getDescrizione());
            statement.setString(4, entity.getNomeConto());
            statement.setInt(5, entity.getId());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(SpeseVariabili entity) {
        deleteById(entity.getChiave());
    }

    @Override
    public void deleteById(AbstractMap.SimpleEntry<String, Integer> stringIntegerSimpleEntry) {
        String sql = "DELETE FROM SpeseVariabili WHERE nomeConto=? and id=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, stringIntegerSimpleEntry.getKey());
            statement.setInt(2, stringIntegerSimpleEntry.getValue());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM SpeseVariabili";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public ObservableList<SpeseVariabili> findBynomeContoandDate(String nomeConto, java.sql.Date data){
        String sql = "SELECT * FROM SpeseVariabili WHERE nomeConto=? AND gg=?";
        ObservableList<SpeseVariabili> ret= FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nomeConto);
            statement.setDate(2, data);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret.add(new SpeseVariabili(nomeConto, rs.getDouble("amount"), rs.getDate("gg"), rs.getString("descrizione"), rs.getInt("id")));
            }
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

}
