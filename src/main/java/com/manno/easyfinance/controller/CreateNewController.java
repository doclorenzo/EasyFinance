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
import java.text.ParseException;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;

public class CreateNewController {

    @FXML
    private TextField nomeContoField;
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
    ObservableList<String> accounts;
    PGSimpleDataSource dataSource;
    InitialPageController parentController;

    boolean flag=true;
    String nameConto;

    public void initDataSource(PGSimpleDataSource dataSource, ObservableList<String> accounts, InitialPageController parentController) {
        this.dataSource=dataSource;
        this.accounts=accounts;
        this.parentController=parentController;
    }

    @FXML
    public void initialize() {
        spesaColDesc.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        spesaColAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        speseFisseObservableList = FXCollections.observableArrayList();
        spesaTable.setItems(speseFisseObservableList);
        spesaTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Single click
                // Get the selected item
                SpeseFisse selectedSpesa = spesaTable.getSelectionModel().getSelectedItem();
                if (selectedSpesa != null) {
                    // Remove the selected item from the data model
                    speseFisseObservableList.remove(selectedSpesa);
                }
            }
        });
    }

    @FXML
    private void handleAggiungiSpesa() {
        if (!spesaDescField.getText().isEmpty() && !spesaAmountField.getText().isEmpty() && !nomeContoField.getText().isEmpty()) {
            if(flag) {
                nameConto=nomeContoField.getText();
                flag=false;
            }
            if(nomeContoField.getText().equals(nameConto)) {
                speseFisseObservableList.add(new SpeseFisse(nomeContoField.getText(), spesaDescField.getText(), Double.parseDouble(spesaAmountField.getText())));
                spesaDescField.setText("");
                spesaAmountField.setText("");
            }
        }
    }

    @FXML
    public void handleSubmit() throws ParseException, SQLException, IOException {

        //creazione account
        if(!nomeContoField.getText().isEmpty() && !monthlyIncomeFiled.getText().isEmpty()){
            Account newAccount = new Account(nomeContoField.getText(), Double.parseDouble(monthlyIncomeFiled.getText()), 0);
            AccountRepository saveNewAccount=new AccountRepository(dataSource);
            saveNewAccount.save(newAccount);

            SpeseFisseRepository saveNewSpeseFisseList=new SpeseFisseRepository(dataSource);
            for(SpeseFisse sf : speseFisseObservableList){
                saveNewSpeseFisseList.save(sf);
            }

            accounts.add(newAccount.getNomeConto());
            showAutoDismissAlert(parentController.getGiga(),"Account creato con successo", Color.DARKGREEN);
            parentController.handleAccount();
        }
    }


}
