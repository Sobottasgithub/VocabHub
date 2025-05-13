package org.example.vocabhub;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.vocabhub.utils.LanguageAutoUpdater;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.logging.Logger;
import java.util.logging.Level;

public class Main extends Application {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    @Override
    public void start(Stage stage) throws IOException {
        LOGGER.log(Level.INFO, "Building App...");
        LanguageAutoUpdater languageAutoUpdater = new LanguageAutoUpdater(Main.class.getResource("languages.json").getPath());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main_window.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 400);
        stage.setTitle("VocabHub");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
