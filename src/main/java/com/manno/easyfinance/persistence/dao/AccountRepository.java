package com.manno.easyfinance.persistence.dao;
import com.manno.easyfinance.persistence.model.Account;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class AccountRepository implements Repository<Account,String>{

    private final PGSimpleDataSource dataSource;

    public AccountRepository(PGSimpleDataSource dataSource) throws SQLException {
        this.dataSource=dataSource;
        checkTable();
    }

    private void checkTable() throws SQLException {
        String sql = "SELECT * FROM Account LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
        } catch (SQLException e) {
            throw new SQLException("table Account doesn't exists or not populated");
        }
    }

    @Override
    public Optional<Account> findById(String s) {
        String sql = "SELECT * FROM Account WHERE nomeConto=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, s);
            ResultSet rs = statement.executeQuery();

            if (!rs.next()) {
                return Optional.empty();
            }

            return Optional.of(new Account(rs.getString("nomeConto"), rs.getDouble("monthlyIncome"), rs.getDouble("bilancio"), rs.getDate("dataCreazione")));
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Iterable<Account> findAll() {
        String sql = "SELECT * FROM Account";
        List<Account> accountList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                accountList.add(new Account(rs.getString("nomeConto"), rs.getDouble("monthlyIncome"), rs.getDouble("bilancio"), rs.getDate("dataCreazione")));
            }
            return accountList;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Account save(Account entity) {
        if (Objects.isNull(entity.getNomeConto())) {
            return insert(entity);
        }

        Optional<Account> account = findById(entity.getNomeConto());
        if (account.isEmpty()) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    private Account insert(Account entity) {
        String sql = "INSERT INTO Account (nomeConto, monthlyIncome, bilancio) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, entity.getNomeConto());
            statement.setDouble(2, entity.getMonthltyIncome());
            statement.setDouble(3, entity.getBilancio());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Account update(Account entity) {
        String sql = "UPDATE Account SET monthlyIncome=?, bilancio=? WHERE nomeConto=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, entity.getMonthltyIncome());
            statement.setDouble(2, entity.getBilancio());
            statement.setString(3, entity.getNomeConto());
            statement.executeUpdate();
            return entity;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void delete(Account entity) {
        deleteById(entity.getNomeConto());
    }

    @Override
    public void deleteById(String s) {
        String sql = "DELETE FROM Account WHERE nomeConto=?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, s);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        String sql = "DELETE FROM Account";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
