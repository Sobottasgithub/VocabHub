package org.example.vocabhub.utils;

import java.io.*;

import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.jsoup.*;
import org.jsoup.nodes.*;

public class LanguageAutoUpdater {
    private static final Logger LOGGER = Logger.getLogger(LanguageAutoUpdater.class.getName());

    Paths paths = new Paths();

    public LanguageAutoUpdater(String fileName) throws IOException {
        File file = new File(fileName);
        List<String> languages = getAllLanguages();
        if (!languages.isEmpty()) {
           saveLanguagesToFile(file, languages);
        }
    }

    public List<String> getAllLanguages() {
        try {
            Document htmlDocument = Jsoup.connect("https://en.wikipedia.org/wiki/List_of_ISO_639_language_codes").get();
            Element table = htmlDocument.selectFirst("table[id=Table] tbody");

            List<String> languages = new ArrayList<>();
            for (int index = 0; index < table.childrenSize(); index++) {
                Element tableRow = table.select("tr").get(index).selectFirst("td");
                if (tableRow != null) {
                    languages.add(tableRow.selectFirst("a").text());
                }
            }
            return languages;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Auto updater failed");
            return new ArrayList<>();
        }
    }

    public void saveLanguagesToFile(File file, List<String> languages) {
        File absolutePath = new File(FileSystems.getDefault()
                .getPath(paths.getUserDataDir() + "/languages.json")
                .normalize()
                .toAbsolutePath()
                .toString()
        );

        if (!absolutePath.exists()) {
            File path = new File(paths.getUserDataDir());
            path.mkdirs();
            try {
                absolutePath.createNewFile();
            } catch (java.io.IOException e) {
                LOGGER.log(Level.WARNING, "Couldn't create dir or file: " + absolutePath);
            };
        }

        System.out.println(absolutePath);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(absolutePath, languages);
        } catch (DatabindException e) {
        } catch (StreamReadException e) {
        } catch (IOException e) {}
    }
}

