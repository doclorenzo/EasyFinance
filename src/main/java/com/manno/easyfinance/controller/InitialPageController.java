package com.manno.easyfinance.controller;

import com.manno.easyfinance.persistence.dao.AccountRepository;
import com.manno.easyfinance.persistence.model.Account;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.postgresql.ds.PGSimpleDataSource;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

import static com.manno.easyfinance.Animations.AutoDismissAlert.showAutoDismissAlert;


public class InitialPageController {

    public AnchorPane getGiga() {
        return giga;
    }

    @FXML private AnchorPane giga;
    @FXML private ListView<String> accountListView;
    @FXML private Label account;
    @FXML private VBox wrapper;
    @FXML private TextArea welcome;
    private ObservableList<Account> accounts;
    private ObservableList<String> accountsName;
    PGSimpleDataSource dataSource;
    private AccountRepository accountRepository;

    public void initDataSource(PGSimpleDataSource dataSource) throws SQLException {
        this.dataSource=dataSource;
        this.accountRepository=new AccountRepository(dataSource);
        Iterable<Account> savedAccounts=accountRepository.findAll();
        for(Account a:savedAccounts) accounts.add(a);
        accountsName.setAll(accounts.stream().map(Account::getNomeConto).toList());
    }

    @FXML
    public void initialize() throws IOException{

        accounts=FXCollections.observableArrayList();
        accountsName=FXCollections.observableArrayList();
        accountListView.setItems(accountsName);

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editItem = new MenuItem("Edit");

        accountListView.setOnMouseClicked(event -> {
            try {
                handleMouseClick(event);
            } catch (IOException | SQLException e) {
                throw new RuntimeException(e);
            }
        });

        editItem.setOnAction(event -> {
            String selectedItem = accountListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    showEditMenu(selectedItem);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        contextMenu.getItems().add(editItem);

        accountListView.setOnContextMenuRequested(event -> {
            contextMenu.show(accountListView, event.getScreenX(), event.getScreenY());
        });

    }

    private void handleMouseClick(MouseEvent event) throws IOException, SQLException {
        if (event.getButton() == MouseButton.PRIMARY) {
            String selectedItem = accountListView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                handleLeftClick(selectedItem);
            }
        }
    }

    private void handleLeftClick(String selectedItem) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("detailedPage.fxml"));
        AnchorPane newPage =fxmlLoader.load();

        DetailedPageController controller=fxmlLoader.getController();

        wrapper.getChildren().clear();
        wrapper.getChildren().add(newPage);
        controller.initDataSource(dataSource,selectedItem,accountRepository.findById(selectedItem).get());

    }

    private void showEditMenu(String selectedItem) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("editAccount.fxml"));
        AnchorPane newPage =fxmlLoader.load();

        EditAccountController controller=fxmlLoader.getController();

        wrapper.getChildren().clear();
        wrapper.getChildren().add(newPage);
        controller.initDataSource(dataSource,selectedItem, this);
    }

    @FXML
    public void handleCreateNew() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("createNew.fxml"));
        AnchorPane newPage =fxmlLoader.load();

        //CreateNewController controller=fxmlLoader.getController();

        CreateNewCOntrollerDemo controller = fxmlLoader.getController();
        controller.initDataSource(dataSource, accountsName, this);
        wrapper.getChildren().clear();
        wrapper.getChildren().add(newPage);
    }

    @FXML
    public void handleAccount() throws IOException {
        wrapper.getChildren().clear();
        wrapper.getChildren().add(welcome);
    }

    @FXML
    public void handleDialogDelete(){
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("deleteAccount.fxml"));
            DialogPane view = loader.load();
            DeleteAccountController controller = loader.getController();

            // Create the dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("New Person");
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.setDialogPane(view);

            // Show the dialog and wait until the user closes it
            Optional<ButtonType> clickedButton = dialog.showAndWait();
            if (clickedButton.orElse(ButtonType.CANCEL) == ButtonType.OK) {
                accountsName.remove(controller.getNomeCDel());
                accountRepository.deleteById(controller.getNomeCDel());
                showAutoDismissAlert(getGiga(),"Account rimosso con successo", Color.DARKGREEN);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
