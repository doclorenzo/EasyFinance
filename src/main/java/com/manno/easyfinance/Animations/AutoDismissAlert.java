package com.manno.easyfinance.Animations;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class AutoDismissAlert {

    public static void showAutoDismissAlert(AnchorPane rootPane, String message, Color backgroundColor) {
        // Crea un layout per il messaggio con uno stile CSS
        StackPane alertPane = new StackPane();
        alertPane.setStyle("-fx-background-color: " + toRgbString(backgroundColor) + ";"
                + "-fx-background-radius: 20px;" // Bordi arrotondati
                + "-fx-padding: 20px;" // Padding per ingrandire l'alert
                + "-fx-border-radius: 20px;" // Bordi arrotondati anche per il bordo
                + "-fx-border-color: black;" // Colore del bordo
                + "-fx-border-width: 2px;"); // Spessore del bordo

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: white;"); // Font più grande e testo bianco
        alertPane.getChildren().add(messageLabel);

        // Posiziona l'alertPane in basso a destra all'interno del rootPane
        rootPane.getChildren().add(alertPane);
        AnchorPane.setBottomAnchor(alertPane, 20.0);
        AnchorPane.setRightAnchor(alertPane, 20.0);

        // Timeline per la dissolvenza e la chiusura automatica
        PauseTransition delay = new PauseTransition(Duration.seconds(2)); // Attendi 2 secondi
        delay.setOnFinished(event -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), alertPane);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> rootPane.getChildren().remove(alertPane));
            fadeOut.play();
        });

        delay.play();
    }

    // Metodo per convertire il colore in una stringa CSS
    private static String toRgbString(Color color) {
        int red = (int) (color.getRed() * 255);
        int green = (int) (color.getGreen() * 255);
        int blue = (int) (color.getBlue() * 255);
        return String.format("rgb(%d, %d, %d)", red, green, blue);
    }
}