package com.manno.easyfinance.persistence;

import com.manno.easyfinance.persistence.dao.SpeseVariabiliRepository;
import com.manno.easyfinance.persistence.model.SpeseVariabili;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Objects;

public class TableSpeseVariabiliHandler implements Comparable<TableSpeseVariabiliHandler> {

    private int giorno;
    private double spesatot;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableSpeseVariabiliHandler that = (TableSpeseVariabiliHandler) o;
        return giorno == that.giorno && Double.compare(spesatot, that.spesatot) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(giorno, spesatot);
    }

    @Override
    public String toString() {
        return "TableSpeseVariabiliHandler{" +
                "giorno=" + giorno +
                ", spesatot=" + spesatot +
                '}';
    }

    public int getGiorno() {
        return giorno;
    }

    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    public double getSpesatot() {
        return spesatot;
    }

    public void setSpesatot(double spesatot) {
        this.spesatot = spesatot;
    }

    public TableSpeseVariabiliHandler(int giorno, double spesatot) {
        this.giorno = giorno;
        this.spesatot = spesatot;
    }


    public ObservableList<TableSpeseVariabiliHandler> getDataForTable(PGSimpleDataSource dataSource, String nomeConto, int mese) throws SQLException {
        String sql = "select DATE_TRUNC('day', gg), sum(amount)\n" +
                "from spesevariabili\n" +
                "WHERE EXTRACT(MONTH FROM gg) = ? and nomeConto=?\n" +
                "group by DATE_TRUNC('day', gg)";
        ObservableList<TableSpeseVariabiliHandler> ret= FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mese);
            statement.setString(2, nomeConto);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret.add(new TableSpeseVariabiliHandler(Integer.parseInt(rs.getDate("giorno").toString().substring(8,10)), rs.getDouble("spesatot")));
            }
            Calendar c = Calendar.getInstance();
            c.set(Calendar.MONTH, mese);
            int maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

            for(int i=1; i<=maxDay; i++){
                int finalI = i;
                if(ret.stream().noneMatch(x-> x.getGiorno() == finalI)){
                    ret.add( new TableSpeseVariabiliHandler(finalI, 0));
                }
            }

            ret.sort(TableSpeseVariabiliHandler::compareTo);
            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Override
    public int compareTo(TableSpeseVariabiliHandler o) {
        return this.giorno-o.getGiorno();
    }
}
