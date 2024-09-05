package com.manno.easyfinance.controller;

import com.manno.easyfinance.persistence.TableSpeseVariabiliHandler;
import com.manno.easyfinance.persistence.model.Account;
import com.manno.easyfinance.persistence.model.SpeseVariabili;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.time.LocalDate;

import static com.manno.easyfinance.persistence.staticTableFiller.getDataForTable;

public class DetailedPageController {


    @FXML private TableView<TableSpeseVariabiliHandler> tableBig;
    @FXML private TableColumn<TableSpeseVariabiliHandler, String> giorniCol;
    @FXML private TableColumn<TableSpeseVariabiliHandler, String> speseTotCol;
    @FXML private ComboBox<String> mesiCombo;

    @FXML private TableView<SpeseVariabili> tableLittle;
    @FXML private TableColumn<SpeseVariabili, String> descCol;
    @FXML private TableColumn<SpeseVariabili, String> speseCol;

    @FXML private Label nomeContoLabel;
    @FXML private Label giornoLabel;
    @FXML private Label bilancioLabel;

    ObservableList<String> mesi = FXCollections.observableArrayList("Gennaio", "Febbraio", "Marzo", "Aprile", "Maggio", "Giugno", "Luglio", "Agosto", "Settembre", "Ottobre", "Novembre", "Dicembre");
    PGSimpleDataSource datasource;
    ObservableList<TableSpeseVariabiliHandler> tableSpeseVariabiliHandlers;

    public void initDataSource(PGSimpleDataSource datasource, String nomeConto) throws SQLException {
        this.datasource=datasource;
        tableSpeseVariabiliHandlers=getDataForTable(datasource,nomeConto, LocalDate.now().getMonthValue());
        System.out.println(tableSpeseVariabiliHandlers);
        mesiCombo.setItems(mesi);
        nomeContoLabel.setText(nomeConto);
    }

    @FXML
    public void initialize(){

        giorniCol.setCellValueFactory(new PropertyValueFactory<>("giorno"));
        speseTotCol.setCellValueFactory(new PropertyValueFactory<>("spesatot"));
        tableBig.setItems(tableSpeseVariabiliHandlers);
    }

}
