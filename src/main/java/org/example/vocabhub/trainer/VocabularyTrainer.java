package org.example.vocabhub.trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

public class VocabularyTrainer {

    private final Random randomIndexer = new Random();
    private final VocabularySet vocabularySet;
    private final List<VocabularyPair> vocabularyPairs;
    private final List<VocabularyPair> learnedVocabularies = new ArrayList<>();
    private final List<VocabularyPair> wrongVocabularies = new ArrayList<>();
    private Optional<VocabularyPair> currentVocabularyPair;
    private int currentVocabularyIndex;

    public VocabularyTrainer(VocabularySet vocabularySet) {
        this.vocabularySet = vocabularySet;
        this.vocabularyPairs = new ArrayList<>(vocabularySet.getVocabularies());
        pullNextVocabulary();
    }

    public Optional<VocabularyPair> currentVocabularyPair() {
        return currentVocabularyPair;
    }

    public CheckVocabularyAnswer checkVocabulary(String answer) {
        boolean success = false;
        Optional<VocabularyPair> checkPair = currentVocabularyPair;
        if (currentVocabularyPair.isPresent()) {

            if (checkPair.get().getTarget().equalsIgnoreCase(answer)) {
                handleSuccess();
                success = true;
            } else {
                handleFailure();
            }
            pullNextVocabulary();
            return new CheckVocabularyAnswer(success, checkPair.get().getTarget());
        } else {
            throw new RuntimeException("Training already finished");
        }
    }

    public int getOverallSize() {
        return vocabularySet.getSize();
    }

    public int getRemainingSize() {
        return vocabularyPairs.size();
    }

    public int getLearnedSize() {
        return learnedVocabularies.size();
    }

    public int getFailedSize() {
        return wrongVocabularies.size();
    }

    public boolean finished() {
        return getRemainingSize() == 0;
    }

    private void handleSuccess() {
        if (currentVocabularyPair.isPresent()) {
            learnedVocabularies.add(currentVocabularyPair.get());
            vocabularyPairs.remove(currentVocabularyIndex);
        }

    }

    private void handleFailure() {
        if (currentVocabularyPair.isPresent()) {
            wrongVocabularies.add(currentVocabularyPair.get());
            vocabularyPairs.remove(currentVocabularyIndex);
        }
    }

    private void pullNextVocabulary() {
        if (getRemainingSize() > 0) {
            currentVocabularyIndex = randomIndexer.nextInt(vocabularyPairs.size());
            currentVocabularyPair = Optional.of(vocabularyPairs.get(currentVocabularyIndex));
        } else {
            currentVocabularyPair = Optional.empty();
        }
    }
}
