package com.manno.easyfinance.controller;
import com.manno.easyfinance.persistence.model.Account;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.SQLException;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;

public class CreateNewCOntrollerDemo extends AbstractNewEdit{

    @FXML
    private TextField nomeContoField;

    ObservableList<String> accounts;

    boolean flag=true;

    public void initDataSource(PGSimpleDataSource dataSource, ObservableList<String> accounts, InitialPageController parentController) {
        this.dataSource=dataSource;
        this.parentController=parentController;
        this.accounts=accounts;
    }

    @Override
    public void initialize() {
        super.initialize();
        nomeContoField.textProperty().addListener(((observable, oldValue, newValue) -> nameConto=newValue));
    }

    @Override
    public void handleAggiungiSpesa() {
        if (!nomeContoField.getText().isEmpty()) {
            if(flag) {
                nameConto=nomeContoField.getText();
                flag=false;
            }
            if(nomeContoField.getText().equals(nameConto)) {
                super.handleAggiungiSpesa();
            }
        }
    }

    @FXML
    public Account handleSubmit() throws SQLException, IOException {
        Account newAccount=super.handleSubmitAbstract(0);
        accounts.add(newAccount.getNomeConto());
        showAutoDismissAlert(parentController.getGiga(),"Account creato con successo", Color.DARKGREEN);
        parentController.handleAccount();
        return newAccount;
    }
}


