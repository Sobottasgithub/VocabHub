package org.example.vocabhub.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.vocabhub.statistics.StatisticDataBinder;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class AppdataPersistentFileService {
    private final File filePath;
    private final ObjectMapper objectMapper;

    public AppdataPersistentFileService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        this.filePath = new File(getUserDataDir() + "/statistics.json");
    }

    public Optional<StatisticDataBinder> loadStatisticsDataBinder() {
        if(!this.filePath.isFile()) {
            return Optional.empty();
        }
        try {
            return Optional.of(this.objectMapper.readValue(this.filePath, StatisticDataBinder.class));
        } catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }

    public void saveStatisticDataBinder(StatisticDataBinder statisticDataBinder)  {
        try {
            if(!this.filePath.isFile()) {
                this.filePath.getParentFile().mkdirs();
            }

            this.objectMapper.writeValue(this.filePath, statisticDataBinder);
        } catch (IOException e) {
            //TODO handle
            throw new RuntimeException(e);
        }
    }

    private String getUserDataDir() {
        String os = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        String baseDirectory = System.getProperty("user.home");
        if (os.contains("mac") || os.contains("darwin")) {
            return baseDirectory + "/Library/Application Support/Patrick-Schulze/VocabHub";
        }
        if (os.contains("win")) {
            return baseDirectory + "/AppData/Local/Patrick-Schulze/VocabHub";
        }
        if (os.contains("linux")) {
            return baseDirectory + "/.local/share/Patrick-Schulze/VocabHub";
        }
        return "";
    }
}
