package org.example.vocabhub.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.vocabhub.trainer.VocabularySet;

import java.io.File;
import java.io.IOException;

public class PersistentFileService {
    private final ObjectMapper objectMapper;

    public PersistentFileService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public VocabularySet loadFromFile(File fileName) {
        try {
            return this.objectMapper.readValue(fileName, VocabularySet.class);
        } catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }

    public void saveToFile(File fileLocation, VocabularySet vocabularySet)  {
        try {
            this.objectMapper.writeValue(fileLocation, vocabularySet);
        } catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }
}
