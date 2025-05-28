package org.example.vocabhub.utils;

import org.example.vocabhub.Main;

import java.nio.file.Path;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Paths {
    private static final Logger LOGGER = Logger.getLogger(Paths.class.getName());

    public Paths() {
    }

    public String getOs() {
        String dir = "";
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            dir = "mac";
        } else if (OS.contains("win")) {
            dir = "windows";
        } else if (OS.contains("nux")) {
            dir = "linux";
        }
        return dir;
    }

    public String getUserDataDir() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String baseDirectory = System.getProperty("user.home");
        if (os.contains("mac") || os.contains("darwin")) {
            return baseDirectory + "/Library/Application Support/VocabHub";
        }
        if (os.contains("win")) {
            return baseDirectory + "/AppData/Local/Patrick-Schulze/VocabHub";
        }
        if (os.contains("nux")) {
            return baseDirectory + "/.local/share/VocabHub";
        }
        return "";
    }
}
