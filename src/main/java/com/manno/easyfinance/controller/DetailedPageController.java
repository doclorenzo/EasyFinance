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
import java.util.Optional;
import java.util.stream.Collectors;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;
import static com.manno.easyfinance.controller.DeatiledTableManager.staticTableFiller.getDataForTable;

public class DetailedPageController {


    @FXML private TableView<TableSpeseVariabiliHandler> tableBig;
    @FXML private TableColumn<TableSpeseVariabiliHandler, Integer> giorniCol;
    @FXML private TableColumn<TableSpeseVariabiliHandler, Double> speseTotCol;
    @FXML private ComboBox<String> mesiCombo;

    @FXML private TableView<SpeseVariabili> tableLittle;
    @FXML private TableColumn<SpeseVariabili, String> descCol;
    @FXML private TableColumn<SpeseVariabili, String> speseCol;

    @FXML private Label nomeContoLabel;
    @FXML private Label giornoLabel;
    @FXML private Label bilancioLabel;

    ObservableList<String> mesi = FXCollections.observableArrayList("JANUARY", "FEBRUARY", "MARCH", "APRIL", "MAY", "JUNE", "JULY", "AUGUST", "SEPTEMBER", "OCTOBER", "NOVEMBER", "DICEMBER");
    PGSimpleDataSource datasource;
    ObservableList<TableSpeseVariabiliHandler> tableSpeseVariabiliHandlers;
    ObservableList<SpeseVariabili> speseVarialibliGGList;
    int curMonth;
    String nomeConto;
    Account curAccount;

    SpeseVariabiliRepository dbmanagerSV;
    SpeseFisseRepository dbmanagerSF;
    AccountRepository dbmanagerA;

    public void initDataSource(PGSimpleDataSource datasource, String nomeConto, Account curAccount, AccountRepository accountRepository) throws SQLException {
        this.datasource=datasource;
        this.nomeConto=nomeConto;
        this.curAccount=curAccount;
        this.dbmanagerSV=new SpeseVariabiliRepository(datasource);
        this.dbmanagerSF= new SpeseFisseRepository(datasource);
        this.dbmanagerA=accountRepository;

        mesiCombo.valueProperty().setValue(LocalDate.now().getMonth().toString());
        curMonth=LocalDate.now().getMonthValue();

        if(curMonth<12) mesi.remove(curMonth,12);
        int creazioneMese=curAccount.getDataCreazione().toLocalDate().getMonthValue();
        if(creazioneMese>1) mesi.remove(0,creazioneMese-1);

        tableSpeseVariabiliHandlers.addAll(getDataForTable(datasource,nomeConto, curMonth, curMonth, this.curAccount.getDataCreazione().toLocalDate()));
        nomeContoLabel.setText(nomeConto);
        speseVarialibliGGList.addAll(dbmanagerSV.findBynomeContoandDate(this.nomeConto, Date.valueOf(LocalDate.now())));
        giornoLabel.setText(LocalDate.now().toString());

        bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
    }

    @FXML
    public void initialize(){
        giorniCol.setCellValueFactory(new PropertyValueFactory<>("giorno"));
        speseTotCol.setCellValueFactory(new PropertyValueFactory<>("spesatot"));

        descCol.setCellValueFactory(new PropertyValueFactory<>("descrizione"));
        speseCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

        tableSpeseVariabiliHandlers=FXCollections.observableArrayList();
        speseVarialibliGGList=FXCollections.observableArrayList();

        tableLittle.setItems(speseVarialibliGGList);
        tableBig.setItems(tableSpeseVariabiliHandlers);
        mesiCombo.setItems(mesi);

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
            if (selectedSpesa != null) {
                speseVarialibliGGList.remove(selectedSpesa);
                dbmanagerSV.deleteById(selectedSpesa.getChiave());
                int tmp=LocalDate.parse(giornoLabel.getText()).getDayOfMonth();
                TableSpeseVariabiliHandler t = tableSpeseVariabiliHandlers.stream().filter(x-> x.getGiorno()==tmp).findFirst().get();
                t.subSpesa(selectedSpesa.getAmount());
                tableBig.refresh();
                curAccount.setBilancio(curAccount.getBilancio()+selectedSpesa.getAmount());
                bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
                dbmanagerA.save(curAccount);
            }
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
            LocalDate tmp=LocalDate.of(2024,mese,giorno);
            speseVarialibliGGList.clear();
            speseVarialibliGGList.addAll(dbmanagerSV.findBynomeContoandDate(this.nomeConto, Date.valueOf(tmp)));
            giornoLabel.setText(tmp.toString());
        }
    }

    @FXML
    public void handleComboBox() throws SQLException {
        String newMonthSelected = mesiCombo.getValue();
        int mesi= Month.valueOf(newMonthSelected).getValue();
        tableSpeseVariabiliHandlers.clear();
        tableSpeseVariabiliHandlers.addAll(getDataForTable(datasource, nomeConto, mesi, curMonth, this.curAccount.getDataCreazione().toLocalDate()));

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
            selectedSpesa.setAmount(controller.getAmount());
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

        controller.setDesc("Default");
        // Show the dialog and wait until the user closes it
        Optional<ButtonType> clickedButton = dialog.showAndWait();
        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            LocalDate tmp=LocalDate.parse(giornoLabel.getText());
            SpeseVariabili nuovaSpesa= new SpeseVariabili(nomeConto, controller.getAmount(), Date.valueOf(tmp), controller.getDesc());
            speseVarialibliGGList.add(nuovaSpesa);
            dbmanagerSV.save(nuovaSpesa);
            TableSpeseVariabiliHandler t = tableSpeseVariabiliHandlers.stream().filter(x-> x.getGiorno()==tmp.getDayOfMonth()).findFirst().get();
            t.addSpesa(nuovaSpesa.getAmount());
            tableBig.refresh();
            curAccount.setBilancio(curAccount.getBilancio()-nuovaSpesa.getAmount());
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
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
        }
    }

    public void handleDeposita() throws IOException {

        AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController> retstat=initDialog("Deposita", "deleteAccount.fxml");
        Optional<ButtonType> clickedButton= retstat.getKey();
        DeleteAccountController controller=retstat.getValue();

        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            curAccount.setBilancio(curAccount.getBilancio()+Double.parseDouble(controller.getNomeCDel()));
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
        }
    }

    public void handlePreleva() throws IOException {

        AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController> retstat=initDialog("Preleva", "deleteAccount.fxml");
        Optional<ButtonType> clickedButton= retstat.getKey();
        DeleteAccountController controller=retstat.getValue();

        if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
            curAccount.setBilancio(curAccount.getBilancio()-Double.parseDouble(controller.getNomeCDel()));
            bilancioLabel.setText(String.valueOf(curAccount.getBilancio()));
            dbmanagerA.save(curAccount);
        }

    }

    private AbstractMap.SimpleEntry<Optional<ButtonType> ,DeleteAccountController>initDialog(String title, String filename) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(filename));
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
