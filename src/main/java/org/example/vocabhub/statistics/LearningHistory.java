package org.example.vocabhub.statistics;

import java.util.ArrayList;
import java.util.List;

public class LearningHistory {
    private List<StatisticData> statisticDataCollection = new ArrayList<>();

    public LearningHistory() {}

    public LearningHistory(List<StatisticData> statisticDataCollection) {
        setData(statisticDataCollection);
    }

    public void addData(StatisticData statisticData) {
        statisticDataCollection.add(statisticData);
    }

    public List<StatisticData> getData() {return statisticDataCollection;}

    public void setData(List<StatisticData> statisticDataCollection) {this.statisticDataCollection = statisticDataCollection;}
}
