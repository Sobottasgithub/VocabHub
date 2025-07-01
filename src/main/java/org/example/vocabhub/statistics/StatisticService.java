package org.example.vocabhub.statistics;

import org.example.vocabhub.persistence.PersistentFileService;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.example.vocabhub.config.AppConfig;

public class StatisticService {
    private static final Logger LOGGER = Logger.getLogger(StatisticService.class.getName());

    private final PersistentFileService<LearningHistory> statisticsFileService = new PersistentFileService<>(LearningHistory.class);
    private final AppConfig appConfig = new AppConfig();
    private LearningHistory learningHistory = new LearningHistory();

    public StatisticService() {
        LOGGER.log(Level.INFO, "Initializing statisticService...");

        // Load data if present
        Optional<LearningHistory> learningHistoryOptional = this.statisticsFileService.loadFromFile(this.appConfig.getStatisticsFilePath());
        learningHistoryOptional.ifPresent(dataBinder -> this.learningHistory = dataBinder);
    }

    private enum StatisticType {
        CORRECT,
        MISTAKE
    }

    public int getTotalMistakeCount() {
        return getTotalCountTemplate(StatisticService.StatisticType.MISTAKE);
    }

    public int getTotalCorrectCount() {
        return getTotalCountTemplate(StatisticService.StatisticType.CORRECT);
    }

    public int getMistakeCountByDate(String date) {
        for (StatisticData statistic: this.learningHistory.getData()) {
            if(Objects.equals(statistic.getDate(), date)) {
                return statistic.getMistakeCount();
            }
        }
        return 0;
    }

    public int getCorrectCountByDate(String date) {
        return getCountByDate(StatisticService.StatisticType.CORRECT, date);
    }

    public int getLength() {
        return this.learningHistory.getData().size();
    }

    public ArrayList<String> getDates() {
        ArrayList<String> dates = new ArrayList<>();
        for (StatisticData statistic: this.learningHistory.getData()) {
            dates.add(statistic.getDate());
        }
        return dates;
    }

    public void addData(int mistakesCount, int learnedCount) {
        learningHistory.addData(new StatisticData(
                mistakesCount,
                learnedCount
        ));
        this.statisticsFileService.saveToFile(this.appConfig.getStatisticsFilePath(), this.learningHistory);
    }

    private int getTotalCountTemplate(StatisticService.StatisticType type) {
        int totalCount = 0;
        for (StatisticData statistic: this.learningHistory.getData()) {
            if(type == StatisticService.StatisticType.CORRECT) {
                totalCount = totalCount + statistic.getCorrectCount();
            } else if (type == StatisticService.StatisticType.MISTAKE) {
                totalCount = totalCount + statistic.getMistakeCount();
            } else {
                LOGGER.log(Level.SEVERE, "You need to specify the statistic Type!");
            }
        }
        return totalCount;
    }

    private int getCountByDate(StatisticService.StatisticType type, String date) {
        for (StatisticData statistic: this.learningHistory.getData()) {
            if(Objects.equals(statistic.getDate(), date)) {
                if(type == StatisticService.StatisticType.CORRECT) {
                    return statistic.getCorrectCount();
                } else if (type == StatisticService.StatisticType.MISTAKE) {
                    return statistic.getMistakeCount();
                } else {
                    LOGGER.log(Level.SEVERE, "You need to specify the statistic Type!");
                }
            }
        }
        return 0;
    }
}
