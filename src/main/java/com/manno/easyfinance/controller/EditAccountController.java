package com.manno.easyfinance.controller;

import com.manno.easyfinance.persistence.dao.AccountRepository;
import com.manno.easyfinance.persistence.dao.SpeseFisseRepository;
import com.manno.easyfinance.persistence.model.Account;
import com.manno.easyfinance.persistence.model.SpeseFisse;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;

import java.sql.SQLException;
import java.util.Optional;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;

public class EditAccountController extends AbstractNewEdit{

    @FXML private Label conto;

    ObservableList<SpeseFisse> prexist;

    public void initDataSource(PGSimpleDataSource dataSource, String selectedItem, InitialPageController parentController) throws SQLException {
        this.dataSource=dataSource;
        this.parentController=parentController;
        nameConto=selectedItem;
        saveNewSpeseFisseList=new SpeseFisseRepository(dataSource);
        saveNewAccount=new AccountRepository(dataSource);
        prexist = this.saveNewSpeseFisseList.findBynomeConto(nameConto);
        speseFisseObservableList.addAll(prexist);
        Optional<Account> prexistMonthlyIncome=saveNewAccount.findById(nameConto);
        super.getMonthlyIncomeFiled().setText(Double.toString(prexistMonthlyIncome.get().getMonthltyIncome()));
        conto.setText(nameConto);
    }

    @FXML
    public Account handleSubmit() throws SQLException, IOException {

        for(SpeseFisse x: prexist){
            if(speseFisseObservableList.stream().noneMatch(y-> y.equals(x))){
                saveNewSpeseFisseList.deleteById(x.getChiave());
            }
        }
        Account n=super.handleSubmitAbstract(saveNewAccount.findById(nameConto).get().getBilancio());
        showAutoDismissAlert(parentController.getGiga(),"Account modificato con successo", Color.DARKGREEN);
        parentController.handleAccount();
        return n;
    }
}
