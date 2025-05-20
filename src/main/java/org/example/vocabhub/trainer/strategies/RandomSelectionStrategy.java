package org.example.vocabhub.trainer.strategies;

import org.example.vocabhub.trainer.model.VocabularyPair;

import java.util.List;
import java.util.Optional;
import java.util.Random;

public class RandomSelectionStrategy implements SelectionStrategy {

    private final Random randomIndexer = new Random();

    @Override
    public Optional<VocabularyPair> getNexVocabulary(List<VocabularyPair> vocabularies) {
        if (!vocabularies.isEmpty()) {
            int currentVocabularyIndex = randomIndexer.nextInt(vocabularies.size());
            return Optional.of(vocabularies.get(currentVocabularyIndex));
        } else {
            return Optional.empty();
        }
    }
}
