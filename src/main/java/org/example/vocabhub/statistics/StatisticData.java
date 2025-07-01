package org.example.vocabhub.statistics;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.time.LocalDate;

public class StatisticData {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

    public int mistakeCount = 0;
    public int correctCount = 0;
    public String date = dateTimeFormatter.format(LocalDate.now());

    public StatisticData() {}

    public StatisticData(int mistakes, int correct) {
        this.mistakeCount = mistakes;
        this.correctCount = correct;
    }

    public void addMistakeCount(int mistakes) {
        this.mistakeCount = this.mistakeCount + mistakes;
    }

    public void addCorrectCount(int correct) {
        this.correctCount = this.correctCount + correct;
    }

    public int getMistakeCount() {
        return this.mistakeCount;
    }

    public int getCorrectCount() {
        return this.correctCount;
    }

    public String getDate() {
        return this.date;
    }
}
