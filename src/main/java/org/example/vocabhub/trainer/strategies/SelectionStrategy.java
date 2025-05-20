package org.example.vocabhub.trainer.strategies;

import org.example.vocabhub.trainer.model.VocabularyPair;

import java.util.List;
import java.util.Optional;

public interface SelectionStrategy {
    Optional<VocabularyPair> getNexVocabulary(List<VocabularyPair> vocabularies);
}
