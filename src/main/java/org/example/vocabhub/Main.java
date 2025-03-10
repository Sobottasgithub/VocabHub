package org.example.vocabhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.logging.Logger;
import java.util.logging.Level;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.log(Level.INFO, "Building App...");
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("VocabHub");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}