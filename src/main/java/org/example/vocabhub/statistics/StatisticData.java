package org.example.vocabhub.statistics;

import java.util.Optional;
import java.time.LocalDate;

public class StatisticData {
    private int mistakeCount = 0;
    private int correctCount = 0;
    private LocalDate date = LocalDate.now();

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

    public LocalDate getDate() {
        return this.date;
    }
}
