package org.example.vocabhub.trainer.strategies;

import org.example.vocabhub.trainer.model.VocabularyPair;

import java.util.List;
import java.util.Optional;

public class TopDownStrategy implements SelectionStrategy{
    @Override
    public Optional<VocabularyPair> getNexVocabulary(List<VocabularyPair> vocabularies) {
        if (vocabularies.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(vocabularies.getFirst());
    }
}
