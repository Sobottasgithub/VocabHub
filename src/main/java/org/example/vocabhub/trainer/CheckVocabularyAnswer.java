package org.example.vocabhub.trainer;

public class CheckVocabularyAnswer {
    private final boolean correct;
    private final String rightAnswer;

    public CheckVocabularyAnswer(boolean correct, String rightAnswer) {
        this.correct = correct;
        this.rightAnswer = rightAnswer;
    }

    public boolean isCorrect() {
        return correct;
    }

    public String getRightAnswer() {
        return rightAnswer;
    }
}
