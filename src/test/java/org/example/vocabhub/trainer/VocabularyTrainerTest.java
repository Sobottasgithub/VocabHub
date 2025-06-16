package org.example.vocabhub.trainer;

import org.example.vocabhub.trainer.model.CheckVocabularyAnswer;
import org.example.vocabhub.trainer.model.VocabularyPair;
import org.example.vocabhub.trainer.model.VocabularySet;
import org.example.vocabhub.trainer.strategies.RandomSelectionStrategy;
import org.example.vocabhub.trainer.strategies.TopDownStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VocabularyTrainerTest {

    private VocabularyTrainer trainer;
    private VocabularySet vocabularySet;

    @BeforeEach
    void setUp() {
        // Create a VocabularySet with defined source and target language
        vocabularySet = new VocabularySet("English", "German");
        
        // Add vocabulary pairs to the set
        VocabularyPair pair1 = new VocabularyPair("cat", "Katze");
        VocabularyPair pair2 = new VocabularyPair("dog", "Hund");
        vocabularySet.addVocabularyPair(pair1);
        vocabularySet.addVocabularyPair(pair2);
        
        // Initialize the trainer with the created set
        trainer = new VocabularyTrainer(vocabularySet, new TopDownStrategy());
    }
    
    @Test
    void testLanguageProperties() {
        // Check language properties
        assertEquals("English", vocabularySet.getSourceLanguage());
        assertEquals("German", vocabularySet.getTargetLanguage());
    }

    @Test
    void testInitialSetup() {
        // Check the initial state of the trainer
        assertEquals(2, trainer.getOverallSize());
        assertEquals(2, trainer.getRemainingSize());
        assertEquals(0, trainer.getLearnedSize());
        assertEquals(0, trainer.getFailedSize());
        assertFalse(trainer.finished());
    }

    @Test
    void testCheckVocabularyCorrectAnswer() {
        // Check correct answer handling
        String vocabulary = trainer.currentVocabulary().orElseThrow();
        CheckVocabularyAnswer result = trainer.checkVocabulary(vocabularySet.getVocabularies().getFirst().getTarget());

        assertTrue(result.isCorrect());
        assertEquals(vocabulary, vocabularySet.getVocabularies().getFirst().getSource());
        assertEquals(1, trainer.getLearnedSize());
        assertEquals(1, trainer.getRemainingSize());
    }

    @Test
    void testCheckVocabularyWrongAnswer() {
        // Check incorrect answer handling
        String vocabulary = trainer.currentVocabulary().orElseThrow();
        CheckVocabularyAnswer result = trainer.checkVocabulary("wrong answer");

        assertFalse(result.isCorrect());
        assertEquals(vocabulary, vocabularySet.getVocabularies().getFirst().getSource());
        assertEquals(vocabularySet.getVocabularies().getFirst().getTarget(), result.getRightAnswer());
        assertEquals(1, trainer.getFailedSize());
        assertEquals(1, trainer.getRemainingSize());
    }

    @Test
    void testFinishedTraining() {
        // Check training completion after answering all questions
        trainer.checkVocabulary(trainer.currentVocabulary().orElseThrow());
        trainer.checkVocabulary(trainer.currentVocabulary().orElseThrow());

        assertTrue(trainer.finished());
        assertEquals(0, trainer.getRemainingSize());
    }

    @Test
    void testEmptyOptionalWhenNoVocabulariesLeft() {
        // Answer all vocabulary questions to put the trainer in a state 
        // where no vocabularies are left
        trainer.checkVocabulary(trainer.currentVocabulary().orElseThrow());
        trainer.checkVocabulary(trainer.currentVocabulary().orElseThrow());
        
        // Check if currentVocabularyPair() returns Optional.empty()
        assertTrue(trainer.finished());
        assertTrue(trainer.currentVocabulary().isEmpty());
        assertFalse(trainer.currentVocabulary().isPresent());
    }
}