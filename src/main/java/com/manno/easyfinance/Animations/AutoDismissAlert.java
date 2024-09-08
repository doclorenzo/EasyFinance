package com.manno.easyfinance.Animations;

import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class AutoDismissAlert {

    public static void showAutoDismissAlert(AnchorPane rootPane, String message, Color textColor) {

        HBox alertBox = new HBox();
        alertBox.setStyle("-fx-background-color: #333; -fx-padding: 15px; -fx-background-radius: 10; -fx-border-radius: 10; ");
        alertBox.setAlignment(Pos.CENTER_LEFT);
        alertBox.setSpacing(10);

        // Label con il messaggio
        Label alertLabel = new Label(message);
        alertLabel.setTextFill(textColor);  // Colore del testo personalizzabile
        alertLabel.setFont(new Font("Roboto", 16));
        alertLabel.setStyle("-fx-font-weight: bold;");  // Testo in grassetto

        // Pulsante "X" per la chiusura manuale
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> rootPane.getChildren().remove(alertBox));

        alertBox.getChildren().addAll(alertLabel, closeButton);

        rootPane.getChildren().add(alertBox);
        AnchorPane.setBottomAnchor(alertBox, 20.0);
        AnchorPane.setRightAnchor(alertBox, 20.0);

        // Animazione di dissolvenza che inizia dopo 2 secondi
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), alertBox);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(2));  // Ritarda l'inizio della dissolvenza
        fadeOut.setOnFinished(event -> rootPane.getChildren().remove(alertBox));  // Rimuove l'alert alla fine
        fadeOut.play();
    }
}