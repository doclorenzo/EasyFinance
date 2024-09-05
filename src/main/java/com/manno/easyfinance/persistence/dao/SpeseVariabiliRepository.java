package com.manno.easyfinance.persistence.dao;

import com.manno.easyfinance.persistence.model.SpeseFisse;
import com.manno.easyfinance.persistence.model.SpeseVariabili;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ResultSet rs = statement.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("table Account doesn't exists or not populated");
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

            return Optional.of(new SpeseVariabili(rs.getString("nomeConto"), rs.getString("descrizione"), rs.getDouble("amount")));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Iterable<SpeseVariabili> findAll() {
        return null;
    }

    @Override
    public SpeseVariabili save(SpeseVariabili entity) {
        return null;
    }

    @Override
    public void delete(SpeseVariabili entity) {

    }

    @Override
    public void deleteById(AbstractMap.SimpleEntry<String, Integer> stringIntegerSimpleEntry) {

    }

    @Override
    public void deleteAll() {

    }
}
