package org.example.vocabhub.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

public class PersistentFileService<T> {
    private final ObjectMapper objectMapper;
    private final Class<T> modelClass;
    public PersistentFileService(Class<T> modelClass) {
        this.modelClass = modelClass;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public Optional<T> loadFromFile(Path fileLocation) {
        try {
            return Optional.of(this.objectMapper.readValue(fileLocation.toFile(), modelClass));
        } catch (MismatchedInputException e) {
            return Optional.empty();
        }
        catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }

    public void saveToFile(Path fileLocation, T model)  {
        try {
            this.objectMapper.writeValue(fileLocation.toFile(), model);
        } catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }
}
