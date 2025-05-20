package org.example.vocabhub.trainer;

import org.example.vocabhub.trainer.model.CheckVocabularyAnswer;
import org.example.vocabhub.trainer.model.VocabularyPair;
import org.example.vocabhub.trainer.model.VocabularySet;
import org.example.vocabhub.trainer.strategies.SelectionStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VocabularyTrainer {

    private final VocabularySet vocabularySet;
    private final List<VocabularyPair> vocabularyPairs;
    private final List<VocabularyPair> learnedVocabularies = new ArrayList<>();
    private final List<VocabularyPair> wrongVocabularies = new ArrayList<>();
    private final SelectionStrategy selectionStrategy;
    private Optional<VocabularyPair> currentVocabularyPair;

    public VocabularyTrainer(VocabularySet vocabularySet, SelectionStrategy strategy) {
        this.vocabularySet = vocabularySet;
        this.vocabularyPairs = new ArrayList<>(vocabularySet.getVocabularies());
        this.selectionStrategy = strategy;
        pullNextVocabulary();
    }

    public Optional<String> currentVocabulary() {
        return currentVocabularyPair.map(VocabularyPair::getSource);
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
            VocabularyPair pair = currentVocabularyPair.get();
            learnedVocabularies.add(pair);
            vocabularyPairs.remove(pair);
        }
    }

    private void handleFailure() {
        if (currentVocabularyPair.isPresent()) {
            wrongVocabularies.add(currentVocabularyPair.get());
            vocabularyPairs.remove(currentVocabularyPair.get());
        }
    }

    private void pullNextVocabulary() {
        currentVocabularyPair = selectionStrategy.getNexVocabulary(List.copyOf(vocabularyPairs));
    }
}
