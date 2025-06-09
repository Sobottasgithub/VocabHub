package org.example.vocabhub.config;

import org.example.vocabhub.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AppConfig {
    private static final Logger LOGGER = Logger.getLogger(Controller.class.getName());
    private static final String STATISTICS_FILENAME = "statistics.json";
    private static final String LANGUAGES_FILENAME = "languages.json";
    private LanguageAutoUpdater languageAutoUpdater;

    public AppConfig() {
        try {
            createDiresAndFiles();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        this.languageAutoUpdater = new LanguageAutoUpdater(getLanguagesFilePath());
    }

    private Path getUserDataDir() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String baseDirectory = System.getProperty("user.home");
        if (os.contains("mac") || os.contains("darwin")) {
            return Path.of(baseDirectory, "Library", "Application Support", "Patrick-Schulze", "VocabHub");
        }
        if (os.contains("win")) {
            return Path.of(baseDirectory, "AppData", "Local", "Patrick-Schulze", "VocabHub");
        }
        if (os.contains("linux")) {
            return Path.of(baseDirectory, ".local", "share", "Patrick-Schulze", "VocabHub");
        }
        return Path.of(".");
    }

    public Path getStatisticsFilePath() {
        return Paths.get(getUserDataDir().toString(), STATISTICS_FILENAME);
    }


    public List<String> getLanguages() {
        return languageAutoUpdater.getLanguages();
    }

    private Path getLanguagesFilePath() {
        return Paths.get(getUserDataDir().toString(), LANGUAGES_FILENAME);
    }

    private void createDiresAndFiles() throws IOException {
        if (!Files.exists(getUserDataDir())) {
            Files.createDirectories(getUserDataDir());
            LOGGER.info("Directories created: " + getUserDataDir());
        }

        if (!Files.exists(getStatisticsFilePath())) {
            Files.createFile(getStatisticsFilePath());
            LOGGER.info("Statistics file created: " + getStatisticsFilePath());
        }

        if (!Files.exists(getLanguagesFilePath())) {
            Files.createFile(getLanguagesFilePath());
            LOGGER.info("Languages file created: " + getLanguagesFilePath());
        }
    }
}
