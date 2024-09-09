package com.manno.easyfinance.controller.DeatiledTableManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Calendar;

public class staticTableFiller {

    static public ObservableList<TableSpeseVariabiliHandler> getDataForTable(PGSimpleDataSource dataSource, String nomeConto, int mese, int currentMonth, LocalDate dataCreazione, int year) {
        String sql = "select DATE_TRUNC('day', gg) as giorno , sum(amount) as spesatot from spesevariabili WHERE EXTRACT(MONTH FROM gg) = ? and EXTRACT(YEAR FROM gg) = ? and nomeConto=? group by DATE_TRUNC('day', gg)";
        ObservableList<TableSpeseVariabiliHandler> ret= FXCollections.observableArrayList();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, mese);
            statement.setInt(2, year);
            statement.setString(3, nomeConto);
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                ret.add(new TableSpeseVariabiliHandler(Integer.parseInt(rs.getDate("giorno").toString().substring(8,10)), rs.getDouble("spesatot")));
            }

            int maxDay;
            if(mese==currentMonth && year==LocalDate.now().getYear()){
                maxDay= LocalDate.now().getDayOfMonth();
            }
            else{
                Calendar c = Calendar.getInstance();
                c.set(Calendar.MONTH, mese-1);
                maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
            }
            int minDay=1;

            if(mese==dataCreazione.getMonthValue() && year==dataCreazione.getYear()){
                minDay=dataCreazione.getDayOfMonth();
            }

            for(int i=minDay; i<=maxDay; i++){
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

}
