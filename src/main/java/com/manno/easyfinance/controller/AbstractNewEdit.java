package com.manno.easyfinance.controller;

import com.manno.easyfinance.persistence.dao.AccountRepository;
import com.manno.easyfinance.persistence.dao.SpeseFisseRepository;
import com.manno.easyfinance.persistence.model.Account;
import com.manno.easyfinance.persistence.model.SpeseFisse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.SQLException;


import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;

public abstract class AbstractNewEdit {

    @FXML
    private TextField monthlyIncomeFiled;
    @FXML
    private TextField spesaDescField;
    @FXML
    private TextField spesaAmountField;

    @FXML
    private TableView<SpeseFisse> spesaTable;
    @FXML
    private TableColumn<SpeseFisse, String> spesaColDesc;
    @FXML
    private TableColumn<SpeseFisse, Double> spesaColAmount;

    ObservableList<SpeseFisse> speseFisseObservableList;
    PGSimpleDataSource dataSource;
    InitialPageController parentController;
    String nameConto;
    AccountRepository saveNewAccount;
    SpeseFisseRepository saveNewSpeseFisseList;

    @FXML
    public void initialize() {

        spesaColDesc.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        spesaColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        speseFisseObservableList = FXCollections.observableArrayList();
        spesaTable.setItems(speseFisseObservableList);

        spesaTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click
                SpeseFisse selectedSpesa = spesaTable.getSelectionModel().getSelectedItem();
                if (selectedSpesa != null) {
                    speseFisseObservableList.remove(selectedSpesa);
                }
            }
        });
    }

    @FXML
    public void handleAggiungiSpesa(){
        if(!spesaDescField.getText().isEmpty() && !spesaAmountField.getText().isEmpty()) {
            try{
                double val=Double.parseDouble(spesaAmountField.getText());
                if(val<=0) throw new NumberFormatException("negativo");
            }
            catch (NumberFormatException e){
                showAutoDismissAlert(parentController.getGiga(),"Importo non valido", Color.RED);
                throw new RuntimeException(e);
            }
            speseFisseObservableList.add(new SpeseFisse(nameConto, spesaDescField.getText(), Double.parseDouble(spesaAmountField.getText())));
            spesaDescField.setText("");
            spesaAmountField.setText("");
        }
    }

    public TextField getMonthlyIncomeFiled() {
        return monthlyIncomeFiled;
    }

    public TextField getSpesaDescField() {
        return spesaDescField;
    }

    public TextField getSpesaAmountField() {
        return spesaAmountField;
    }

    public TableColumn<SpeseFisse, String> getSpesaColDesc() {
        return spesaColDesc;
    }

    public TableColumn<SpeseFisse, Double> getSpesaColAmount() {
        return spesaColAmount;
    }

    public ObservableList<SpeseFisse> getSpeseFisseObservableList() {
        return speseFisseObservableList;
    }

    public PGSimpleDataSource getDataSource() {
        return dataSource;
    }

    public InitialPageController getParentController() {
        return parentController;
    }

    public TableView<SpeseFisse> getSpesaTable() {
        return spesaTable;
    }


    public Account handleSubmitAbstract(double bil) throws SQLException{
        Account newAccount;
        if(!monthlyIncomeFiled.getText().isEmpty()){
            double val;
            try{
                val=Double.parseDouble(monthlyIncomeFiled.getText());
                if(val<0) throw new NumberFormatException("negativo");
            } catch (NumberFormatException e) {
                showAutoDismissAlert(parentController.getGiga(),"Incasso mensile non valido", Color.RED);
                throw new RuntimeException(e);
            }
            newAccount = new Account(nameConto, Double.parseDouble(monthlyIncomeFiled.getText()), bil);
            saveNewAccount=new AccountRepository(dataSource);
            saveNewAccount.save(newAccount);
            saveNewSpeseFisseList=new SpeseFisseRepository(dataSource);
            for(SpeseFisse sf : speseFisseObservableList){
                saveNewSpeseFisseList.save(sf);
            }
            return newAccount;
        }
        showAutoDismissAlert(parentController.getGiga(),"Inserisci incasso mensile", Color.ORANGE);
        return null;
    }
}
