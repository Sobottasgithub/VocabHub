package org.example.vocabhub.config;

import java.io.*;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.vocabhub.persistence.PersistentFileService;
import org.jsoup.*;
import org.jsoup.nodes.*;

class LanguageAutoUpdater {
    private static final Logger LOGGER = Logger.getLogger(LanguageAutoUpdater.class.getName());
    private final PersistentFileService<LanguageList> languagesFileService = new PersistentFileService<>(LanguageList.class);
    private final Path languageFilePath;
    private LanguageList languages = new LanguageList();

    LanguageAutoUpdater(Path languageFilePath) {
        this.languageFilePath = languageFilePath;
        loadLanguages();
    }

    LanguageList getLanguages() {
        return languages;
    }

    private LanguageList fetchLanguages() {
        try {
            Document htmlDocument = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes").get();
            Element table = htmlDocument.selectFirst("table[id=Table] tbody");

            LanguageList languages = new LanguageList();
            for (int index = 0; index < table.childrenSize(); index++) {
                Element tableRow = table.select("tr").get(index).selectFirst("td");
                if (tableRow != null) {
                    languages.add(tableRow.selectFirst("a").text());
                }
            }
            return languages;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Auto updater failed");
            return new LanguageList();
        }
    }

    private void loadLanguages() {
        languagesFileService.loadFromFile(languageFilePath).ifPresent(list -> this.languages = list);
        if (languages.isEmpty()) {
            this.languages = fetchLanguages();
            this.languagesFileService.saveToFile(languageFilePath, languages);
        }
    }

}

