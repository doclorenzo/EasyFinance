package com.manno.easyfinance.controller;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class AddSpesaVariabileController {

    @FXML
    private TextField descrizione;
    @FXML
    private TextField importo;
    String desc;
    double amount;

    @FXML
    public void initialize(){
        descrizione.textProperty().addListener(((observable, oldValue, newValue) -> desc=newValue));
        importo.textProperty().addListener(((observable, oldValue, newValue) -> amount=Double.parseDouble(newValue)));
    }

    public String getDesc(){
        return desc;
    }

    public double getAmount(){
        return amount;
    }

    public void setAmount(Double amount){
        this.amount=amount;
        importo.setText(amount.toString());
    }

    public void setDesc(String desc){
        this.desc=desc;
        descrizione.setText(desc);
    }

}
