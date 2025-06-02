package org.example.vocabhub.statistics;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import java.util.logging.Logger;
import java.util.logging.Level;

public class StatisticDataBinder {
    private static final Logger LOGGER = Logger.getLogger(StatisticDataBinder.class.getName());

    private final List<StatisticData> statisticDataCollection = new ArrayList<>();

    public StatisticDataBinder() {}

    public int getTotalMistakeCount() {
        return getTotalCountTemplate(StatisticType.MISTAKE);
    }

    public int getTotalCorrectCount() {
        return getTotalCountTemplate(StatisticType.CORRECT);
    }

    public int getMistakeCountByDate(String date) {
        for (StatisticData statistic: statisticDataCollection) {
            if(statistic.getDate() == date) {
                return statistic.getMistakeCount();
            }
        }
        return 0;
    }

    public int getCorrectCountByDate(String date) {
        return getCountByDate(StatisticType.CORRECT, date);
    }

    public int getEntryCount() {
        return statisticDataCollection.size();
    }

    public ArrayList<String> getDates() {
        ArrayList<String> dates = new ArrayList<>();
        for (StatisticData statistic: statisticDataCollection) {
            dates.add(statistic.getDate());
        }
        return dates;
    }

    public void addData(StatisticData statisticData) {
        statisticDataCollection.add(statisticData);
    }

    private int getTotalCountTemplate(StatisticType type) {
        int totalCount = 0;
        for (StatisticData statistic: statisticDataCollection) {
            if(type == StatisticType.CORRECT) {
                totalCount = totalCount + statistic.getCorrectCount();
            } else if (type == StatisticType.MISTAKE) {
                totalCount = totalCount + statistic.getMistakeCount();
            } else {
                LOGGER.log(Level.SEVERE, "You need to specify the statistic Type!");
            }
        }
        return totalCount;
    }

    private int getCountByDate(StatisticType type, String date) {
        for (StatisticData statistic: statisticDataCollection) {
            if(statistic.getDate() == date) {
                if(type == StatisticType.CORRECT) {
                    return statistic.getCorrectCount();
                } else if (type == StatisticType.MISTAKE) {
                    return statistic.getMistakeCount();
                } else {
                    LOGGER.log(Level.SEVERE, "You need to specify the statistic Type!");
                }
            }
        }
        return 0;
    }
}
