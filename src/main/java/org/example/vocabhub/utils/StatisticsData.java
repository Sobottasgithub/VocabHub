package org.example.vocabhub.utils;

import org.example.vocabhub.Main;
import org.example.vocabhub.utils.Paths;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.HashMap;
import java.time.LocalDate;

import java.util.logging.Level;
import java.util.logging.Logger;

public class StatisticsData {
    private static final Logger LOGGER = Logger.getLogger(StatisticsData.class.getName());

    Paths paths = new Paths();

    HashMap<String, Object> data;
    File absolutePath = new File(FileSystems.getDefault()
            .getPath(paths.getUserDataDir() + "/statistics.json")
            .normalize()
            .toAbsolutePath()
            .toString()
    );

    public StatisticsData() throws IOException {
        LOGGER.log(Level.INFO, "Initializing Statistics...");

        if (!absolutePath.exists()) {
            File path = new File(paths.getUserDataDir());
            path.mkdirs();
            absolutePath.createNewFile();

            HashMap<String, Object> dataTemplate;
            ObjectMapper mapper = new ObjectMapper();
            try {
                dataTemplate = mapper.readValue(Main.class.getResource("statistics.json"), HashMap.class);
            } catch (IOException e) {
                dataTemplate = new HashMap<String, Object>();
            }

            mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
            try {
                writer.writeValue(absolutePath, dataTemplate);
            } catch (IOException _) {}
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            data = mapper.readValue(absolutePath, HashMap.class);
        } catch (IOException e) {
            data = new HashMap<String, Object>();
        }
    }

    public void addMistakeWithDate(Integer count) {
        HashMap<String, Integer> mistakesWithDate = (HashMap<String, Integer>) data.get("vocabMistakesWithDate");

        String date = LocalDate.now().toString();
        for(int index = 0; index < mistakesWithDate.size(); index++) {
            if(mistakesWithDate.keySet().toArray()[index].toString().matches(date)) {
                count = (Integer) mistakesWithDate.get(date) + count;
            }
        }

        mistakesWithDate.put(date, count);
        data.put("vocabMistakesWithDate", (Object) mistakesWithDate);
        store();
    }

    public HashMap<String, Integer> getMistakesWithDate() {
        return (HashMap<String, Integer>) data.get("vocabMistakesWithDate");
    }

    public Integer getTotalMistakes() {
        HashMap<String, Integer> mistakesWithDate = (HashMap<String, Integer>) data.get("vocabMistakesWithDate");

        int count = 0;
        for(int index = 0; index < mistakesWithDate.size(); index++) {
            count = count + mistakesWithDate.get(mistakesWithDate.keySet().toArray()[index].toString());
        }
        return count;
    }

    public void addTrainedCount(int trainedCount) {
        Object total = (Integer) data.get("vocabTrainedTotal") + trainedCount;
        data.put("vocabTrainedTotal", total);
        store();
    }

    public int getTrainedCount() {
        return (Integer) data.get("vocabTrainedTotal");
    }

    private void store() {
        LOGGER.log(Level.INFO, "Storing statistics to file...");

        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
        try {
            writer.writeValue(absolutePath, data);
        } catch (IOException _) {}
    }
}
