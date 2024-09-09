package com.manno.easyfinance.controller;

import com.manno.easyfinance.controller.DeatiledTableManager.TableSpeseVariabiliHandler;
import com.manno.easyfinance.persistence.dao.AccountRepository;
import com.manno.easyfinance.persistence.dao.SpeseFisseRepository;
import com.manno.easyfinance.persistence.dao.SpeseVariabiliRepository;
import com.manno.easyfinance.persistence.model.Account;
import com.manno.easyfinance.persistence.model.SpeseFisse;
import com.manno.easyfinance.persistence.model.SpeseVariabili;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.AbstractMap;
import java.util.Calendar;
import java.util.Optional;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;
import static com.manno.easyfinance.controller.DeatiledTableManager.staticTableFiller.getDataForTable;

public class DetailedPageController {


    @FXML private TableView<TableSpeseVariabiliHandler> tableBig;
    @FXML private TableColumn<TableSpeseVariabiliHandler, Integer> giorniCol;
    @FXML private TableColumn<TableSpeseVariabiliHandler, Double> speseTotCol;
    @FXML private ComboBox<String> mesiCombo;
    @FXML private ComboBox<Integer> mesiCombo1;

    @FXML private TableView<SpeseVariabili> tableLittle;
    @FXML private TableColumn<SpeseVariabili, String> descCol;
    @FXML private TableColumn<SpeseVariabili, String> speseCol;

    @FXML private Label nomeContoLabel;
    @FXML private Label giornoLabel;
    @FXML private Label bilancioLabel;
    @FXML private Label SMG;
    @FXML private Label risparmi;

    ObservableList<String> finalMesi = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DECEMBER");
    ObservableList<String> mesi= FXCollections.observableArrayList(finalMesi);
    ObservableList<Integer> anni;
    PGSimpleDataSource datasource;
    ObservableList<TableSpeseVariabiliHandler> tableSpeseVariabiliHandlers;
    ObservableList<SpeseVariabili> speseVarialibliGGList;
    int curMonth;
    String nomeConto;
    Account curAccount;
    InitialPageController parentcontroller;
    int maxDay;
    Calendar c;
    double tot;
    int newYearSelected;

    SpeseVariabiliRepository dbmanagerSV;
    SpeseFisseRepository dbmanagerSF;
    AccountRepository dbmanagerA;

    public void initDataSource(InitialPageController giga, PGSimpleDataSource datasource, String nomeConto, Account curAccount, AccountRepository accountRepository) throws SQLException {
        this.parentcontroller=giga;
        this.datasource=datasource;
        this.nomeConto=nomeConto;
        this.curAccount=curAccount;
        this.dbmanagerSV=new SpeseVariabiliRepository(datasource);
        this.dbmanagerSF= new SpeseFisseRepository(datasource);
        this.dbmanagerA=accountRepository;

        mesiCombo.valueProperty().setValue(LocalDate.now().getMonth().toString());
        mesiCombo1.valueProperty().setValue(LocalDate.now().getYear());
        curMonth=LocalDate.now().getMonthValue();

        newYearSelected=curAccount.getDataCreazione().toLocalDate().getYear();

        for(int i=LocalDate.now().getYear(); i>=newYearSelected; i--){
            anni.add(i);
        }
        newYearSelected=LocalDate.now().getYear();
        setTheMonthsCombo();

        tableSpeseVariabiliHandlers.addAll(getDataForTable(datasource,nomeConto, curMonth, curMonth, this.curAccount.getDataCreazione().toLocalDate(), LocalDate.now().getYear()));
        nomeContoLabel.setText(nomeConto);
        speseVarialibliGGList.addAll(dbmanagerSV.findBynomeContoandDate(this.nomeConto, Date.valueOf(LocalDate.now())));
        giornoLabel.setText(LocalDate.now().toString());

        bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));

        c = Calendar.getInstance();
        c.set(Calendar.MONTH, curMonth);
        maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);

        ObservableList<SpeseFisse> spesefisse=dbmanagerSF.findBynomeConto(nomeConto);
        double totalespese= spesefisse.stream().mapToDouble(SpeseFisse::getAmount).sum();
        tot=Math.round((curAccount.getMonthltyIncome()-totalespese)/maxDay*100)/100.0;
        SMG.setText(String.valueOf(tot));
        changeRisparmi();
    }

    public void setTheMonthsCombo(){
        int annocreazione=curAccount.getDataCreazione().toLocalDate().getYear();
        mesi=FXCollections.observableArrayList(finalMesi);
        if(LocalDate.now().getYear()==newYearSelected && annocreazione==newYearSelected){
            if(curMonth<12) mesi.remove(curMonth,12);
            int creazioneMese=curAccount.getDataCreazione().toLocalDate().getMonthValue();
            if(creazioneMese>1) mesi.remove(0,creazioneMese-1);
        }
        else if(LocalDate.now().getYear()==newYearSelected){
            if(curMonth<12) mesi.remove(curMonth,12);
        }
        else if (newYearSelected == annocreazione){
            int creazioneMese=curAccount.getDataCreazione().toLocalDate().getMonthValue();
            if(creazioneMese>1) mesi.remove(0,creazioneMese-1);
        }
        mesiCombo.setItems(mesi);
    }

    public void changeRisparmi(){

        int today=LocalDate.now().getDayOfMonth();
        double totmese= (maxDay-today)*tot;
        double value=Math.round((curAccount.getBilancio()-totmese+tot)*100)/100.0;
        risparmi.setText(String.valueOf(value));
        if(value > 0) risparmi.setStyle("-fx-textFill:Green");
        else if(value==0) risparmi.setStyle("-fx-textFill:Yellow");
        else risparmi.setStyle("-fx-textFill:Red");
    }

    @FXML
    public void initialize(){
        giorniCol.setCellValueFactory(new PropertyValueFactory<>("giorno"));
        speseTotCol.setCellValueFactory(new PropertyValueFactory<>("spesatot"));

        descCol.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        speseCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableSpeseVariabiliHandlers=FXCollections.observableArrayList();
        speseVarialibliGGList=FXCollections.observableArrayList();
        anni=FXCollections.observableArrayList();

        tableLittle.setItems(speseVarialibliGGList);
        tableBig.setItems(tableSpeseVariabiliHandlers);
        mesiCombo.setItems(mesi);
        mesiCombo1.setItems(anni);

        tableBig.setOnMouseClicked(this::handleTableBigClick);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");
        MenuItem deleteItem = new MenuItem("Delete");

        contextMenu.getItems().addAll(editItem, deleteItem);

        editItem.setOnAction(event -> {
            SpeseVariabili selectedSpesa = tableLittle.getSelectionModel().getSelectedItem();
            if (selectedSpesa != null) {
                try {
                    handleEdit(selectedSpesa);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        deleteItem.setOnAction(event -> {
            SpeseVariabili selectedSpesa = tableLittle.getSelectionModel().getSelectedItem();
            dbmanagerSV.deleteById(selectedSpesa.getChiave());
            speseVarialibliGGList.remove(selectedSpesa);
            int tmp=LocalDate.parse(giornoLabel.getText()).getDayOfMonth();
            TableSpeseVariabiliHandler t = tableSpeseVariabiliHandlers.stream().filter(x-> x.getGiorno()==tmp).findFirst().get();
            t.subSpesa(selectedSpesa.getAmount());
            tableBig.refresh();
            curAccount.setBilancio(curAccount.getBilancio()+selectedSpesa.getAmount());
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        });


        tableLittle.setRowFactory(tv -> {
            TableRow<SpeseVariabili> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });

    }

    @FXML
    public void handleTableBigClick(MouseEvent event){
        if (event.getButton() == MouseButton.PRIMARY) {
            int giorno = tableBig.getSelectionModel().getSelectedItem().getGiorno();
            String newMonthSelected=mesiCombo.getValue();
            int mese= Month.valueOf(newMonthSelected).getValue();
            LocalDate tmp=LocalDate.of(newYearSelected,mese,giorno);
            speseVarialibliGGList.clear();
            speseVarialibliGGList.addAll(dbmanagerSV.findBynomeContoandDate(this.nomeConto, Date.valueOf(tmp)));
            giornoLabel.setText(tmp.toString());
        }
    }

    @FXML
    public void handleComboBox1() {
        newYearSelected = mesiCombo1.getValue();
        setTheMonthsCombo();
        tableSpeseVariabiliHandlers.clear();
        tableSpeseVariabiliHandlers.addAll(getDataForTable(datasource, nomeConto, curMonth, curMonth, this.curAccount.getDataCreazione().toLocalDate(), newYearSelected));
    }

    @FXML
    public void handleComboBox() {
        String newMonthSelected = mesiCombo.getValue();
        int mesi= Month.valueOf(newMonthSelected).getValue();
        c.set(Calendar.MONTH, curMonth);
        maxDay = c.getActualMaximum(Calendar.DAY_OF_MONTH);
        tableSpeseVariabiliHandlers.clear();
        tableSpeseVariabiliHandlers.addAll(getDataForTable(datasource, nomeConto, mesi, curMonth, this.curAccount.getDataCreazione().toLocalDate(), newYearSelected));
    }

    public void handleEdit(SpeseVariabili selectedSpesa) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addSpesaVariabile.fxml"));
        DialogPane view = loader.load();
        AddSpesaVariabileController controller = loader.getController();

        controller.setAmount(selectedSpesa.getAmount());
        controller.setDesc(selectedSpesa.getDescrizione());


        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Spesa");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(view);

        double oldValue=selectedSpesa.getAmount();

        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {

            double val;
            try{
                if (controller.getDesc().isEmpty() || controller.getDesc().isBlank()) throw new IllegalArgumentException("Desc Not Valid");
                val=controller.getAmount();
                if(val<=0) throw new IllegalArgumentException("Amount must be positive");
            }
            catch (Exception e){
                showAutoDismissAlert(parentcontroller.getGiga(), "Argomenti non Validi", Color.RED);
                throw new RuntimeException(e);
            }

            selectedSpesa.setAmount(val);
            selectedSpesa.setDescrizione(controller.getDesc());
            tableLittle.refresh();
            dbmanagerSV.save(selectedSpesa);
            int tmp=LocalDate.parse(giornoLabel.getText()).getDayOfMonth();
            TableSpeseVariabiliHandler t = tableSpeseVariabiliHandlers.stream().filter(x-> x.getGiorno()==tmp).findFirst().get();
            t.subSpesa(oldValue);
            t.addSpesa(selectedSpesa.getAmount());
            tableBig.refresh();
            curAccount.setBilancio(curAccount.getBilancio()+oldValue-selectedSpesa.getAmount());
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        }
    }

    @FXML
    public void handleAggiungi() throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("addSpesaVariabile.fxml"));
        DialogPane view = loader.load();
        AddSpesaVariabileController controller = loader.getController();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nuova Spesa");
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(view);

        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            double val;
            try{
                if (controller.getDesc().isEmpty() || controller.getDesc().isBlank()) throw new IllegalArgumentException("Desc Not Valid");
                val=controller.getAmount();
                if(val<=0) throw new IllegalArgumentException("Amount must be positive");
            }
            catch (Exception e){
                showAutoDismissAlert(parentcontroller.getGiga(), "Argomenti non Validi", Color.RED);
                throw new RuntimeException(e);
            }

            LocalDate tmp=LocalDate.parse(giornoLabel.getText());
            SpeseVariabili nuovaSpesa= new SpeseVariabili(nomeConto, val, Date.valueOf(tmp), controller.getDesc());
            speseVarialibliGGList.add(nuovaSpesa);
            dbmanagerSV.save(nuovaSpesa);
            TableSpeseVariabiliHandler t = tableSpeseVariabiliHandlers.stream().filter(x-> x.getGiorno()==tmp.getDayOfMonth()).findFirst().get();
            t.addSpesa(nuovaSpesa.getAmount());
            tableBig.refresh();
            curAccount.setBilancio(curAccount.getBilancio()-val);
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        }
    }

    public void handleStipendioSpeseFisse(){

        ObservableList<SpeseFisse> spesefisse=dbmanagerSF.findBynomeConto(nomeConto);
        double totale= spesefisse.stream().mapToDouble(SpeseFisse::getAmount).sum();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ATTENZIONE");
        alert.setHeaderText(null);
        alert.setContentText("Stai per Aggiungere al tuo bilancio il corrente incasso mensile di:\n"+curAccount.getMonthltyIncome()+
                "€\n e stai per rimuovere il totale delle Spese Fisse:\n"+totale+
                "€\nSe vuoi effettuare delle modifiche fallo prima di premere il pulsante 'OK'\n\n Sicuro di voler procedere?");

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            curAccount.setBilancio(curAccount.getBilancio()+curAccount.getMonthltyIncome()-totale);
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        }
    }

    public void handleDeposita() throws IOException {

        AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController> retstat=initDialog("Deposita");
        Optional<ButtonType> clickedButton= retstat.getKey();
        DeleteAccountController controller=retstat.getValue();

        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            double val;
            try{
                val=Double.parseDouble(controller.getNomeCDel());
                if(val<=0) throw new NumberFormatException("Negativo");
            }
            catch(NumberFormatException e){
                showAutoDismissAlert(this.parentcontroller.getGiga(), "Importo invalido", Color.RED);
                throw new RuntimeException(e);
            }
            curAccount.setBilancio(curAccount.getBilancio()+val);
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        }
    }

    public void handlePreleva() throws IOException {

        AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController> retstat=initDialog("Preleva");
        Optional<ButtonType> clickedButton= retstat.getKey();
        DeleteAccountController controller=retstat.getValue();

        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            double val;
            try{
                val=Double.parseDouble(controller.getNomeCDel());
                if(val<=0) throw new NumberFormatException("Negativo");
            }
            catch(NumberFormatException e){
                showAutoDismissAlert(this.parentcontroller.getGiga(), "Importo invalido", Color.RED);
                throw new RuntimeException(e);
            }
            curAccount.setBilancio(curAccount.getBilancio()-val);
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
            changeRisparmi();
        }

    }

    private AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController> initDialog(String title) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("deleteAccount.fxml"));
        DialogPane view = loader.load();
        DeleteAccountController controller = loader.getController();
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setDialogPane(view);
        controller.setLabel("Importo");
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        return new AbstractMap.SimpleEntry<>(clickedButton, controller);
    }

}
