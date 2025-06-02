package org.example.vocabhub.statistics;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.time.LocalDate;

public class StatisticData {
    final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

    private int mistakeCount = 0;
    private int correctCount = 0;
    private String date = dateTimeFormatter.format(LocalDate.now());

    public StatisticData() {}

    public StatisticData(Optional<Integer> mistakes, Optional<Integer> correct) {
        mistakes.ifPresent(integer -> this.mistakeCount = integer);
        correct.ifPresent(integer -> this.correctCount = integer);
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
