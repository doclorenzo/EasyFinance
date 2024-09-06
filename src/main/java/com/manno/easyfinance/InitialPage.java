package com.manno.easyfinance;

import com.manno.easyfinance.controller.InitialPageController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.postgresql.ds.PGSimpleDataSource;
import java.io.IOException;
import java.sql.SQLException;

public class InitialPage extends Application {

    private static final String JDBC_URL="jdbc:postgresql://localhost:5432/EasyFinance?user=postgres&password=a";

    public void start(Stage stage) throws IOException, SQLException {
        PGSimpleDataSource dataSource = createDataSource();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(InitialPage.class.getResource("InitialPage.fxml"));
        Parent root=fxmlLoader.load();
        InitialPageController controller = fxmlLoader.getController();
        controller.initDataSource(dataSource);
        Scene scene = new Scene(root, 1050, 700);
        stage.setTitle("Easy Finance");
        stage.setScene(scene);
        stage.show();
    }

    private static PGSimpleDataSource createDataSource(){
        final PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl(InitialPage.JDBC_URL);
        return dataSource;
    }

    public static void main(String[] args) {
        launch(args);
    }

}
