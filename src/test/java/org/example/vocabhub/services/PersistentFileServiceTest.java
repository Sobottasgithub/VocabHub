package org.example.vocabhub.services;

import org.example.vocabhub.persistence.PersistentFileService;
import org.example.vocabhub.trainer.VocabularyPair;
import org.example.vocabhub.trainer.VocabularySet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class PersistentFileServiceTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loadFromFile() throws IOException {
        VocabularySet set = new PersistentFileService().loadFromFile(new File("test.json"));
        System.out.println(set);
    }

    @Test
    void saveToFile() throws IOException {
        VocabularySet set = new VocabularySet("German", "English");
        set.addVocabularyPair(new VocabularyPair("Haus", "house"));
        new PersistentFileService().saveToFile(new File("test.json"), set);
    }
}