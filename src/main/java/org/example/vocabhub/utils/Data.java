package org.example.vocabhub.utils;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * can be deleted
 */
@Deprecated
public class Data {
    private static final Logger LOGGER = Logger.getLogger(Data.class.getName());

    HashMap<String, String> data = new HashMap<>();
    String filePath = "";

    public Data() {

    }

    public Data(String filePath) {
        openFile(filePath);
    }

    public HashMap<String, String> openFile(String filePath) {
        LOGGER.log(Level.INFO, "Initializing Data...");
        this.filePath = filePath;
        data = new HashMap<>();
        File jsonFile = new File(filePath.toString());
        ObjectMapper mapper = new ObjectMapper();
        try {
            data = mapper.readValue(jsonFile, HashMap.class);
        } catch (DatabindException e) {
            data = new HashMap<String, String>();
        } catch (StreamReadException e) {
            data = new HashMap<String, String>();
        } catch (IOException e) {
            data = new HashMap<String, String>();
        }

        // get languages from file
        HashMap<String, String> language = new HashMap<>();
        language.put((String) data.keySet().toArray()[data.size() - 1], (String) data.get(data.keySet().toArray()[data.size() - 1]));
        data.remove((String) data.keySet().toArray()[data.size() - 1]);
        return language;
    }

    public int getSize() {
        return data.size();
    }

    public boolean isEmpty() {
        return data.isEmpty();
    }

    public void put(String key, String value) {
        data.put(key, value);
    }

    public String getKeyByIndex(int index) {
        return data.keySet().toArray()[index].toString();
    }

    public String getValue(String key) {
        return data.get(key);
    }

    public void saveToFile(String filePath, String language, String translation) {
        LOGGER.log(Level.INFO, "Storing data to file...");
        if(filePath.isEmpty()) {
            filePath = this.filePath;
        }

        data.put(language, translation);

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(new File(filePath), data);
        } catch (DatabindException e) {
        } catch (StreamReadException e) {
        } catch (IOException e) {}
    }
}
