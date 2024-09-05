package com.manno.easyfinance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class DeleteAccountController {

    @FXML private TextField input;
    String nomeCDel;

    @FXML
    public void initialize(){
        input.textProperty().addListener(((observable, oldValue, newValue) -> nomeCDel=newValue));
    }

    public String getNomeCDel(){
        return nomeCDel;
    }

}
