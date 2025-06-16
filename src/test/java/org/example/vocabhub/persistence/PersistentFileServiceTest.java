package org.example.vocabhub.persistence;

import org.example.vocabhub.trainer.model.VocabularyPair;
import org.example.vocabhub.trainer.model.VocabularySet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersistentFileServiceTest {

    private PersistentFileService<VocabularySet> service;
    private File testFile;
    
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        service = new PersistentFileService<VocabularySet>(VocabularySet.class);
        testFile = tempDir.resolve("test-vocab.json").toFile();
    }

    @AfterEach
    void tearDown() {
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    @Test
    void shouldSaveAndLoadVocabularySet() throws IOException {
        // Given
        VocabularySet originalSet = new VocabularySet("German", "English");
        originalSet.addVocabularyPair(new VocabularyPair("Haus", "house"));
        originalSet.addVocabularyPair(new VocabularyPair("Auto", "car"));
        
        // When
        service.saveToFile(testFile.toPath(), originalSet);
        
        // Then
        assertTrue(testFile.exists(), "File should have been created");
        assertTrue(testFile.length() > 0, "File should have content");
        
        // When
        VocabularySet loadedSet = service.loadFromFile(testFile.toPath()).get();
        
        // Then
        assertNotNull(loadedSet, "Loaded set should not be null");
        assertEquals(originalSet.getSourceLanguage(), loadedSet.getSourceLanguage(), "Source language should match");
        assertEquals(originalSet.getTargetLanguage(), loadedSet.getTargetLanguage(), "Target language should match");
        assertEquals(originalSet.getVocabularies().size(), loadedSet.getVocabularies().size(), 
                "Number of vocabulary pairs should be equal");
        
        // Check each vocabulary pair
        for (int i = 0; i < originalSet.getVocabularies().size(); i++) {
            VocabularyPair originalPair = originalSet.getVocabularies().get(i);
            VocabularyPair loadedPair = loadedSet.getVocabularies().get(i);
            assertEquals(originalPair.getSource(), loadedPair.getSource(), "Source should match");
            assertEquals(originalPair.getTarget(), loadedPair.getTarget(), "Target should match");
        }
    }
    
    @Test
    void shouldThrowExceptionWhenFileDoesNotExist() {
        // Given
        File nonExistentFile = tempDir.resolve("non-existent-file.json").toFile();
        
        // Then
        assertThrows(RuntimeException.class, () -> {
            service.loadFromFile(nonExistentFile.toPath());
        }, "Loading a non-existent file should throw an exception");
    }
    
    @Test
    void shouldHandleEmptyVocabularySet() throws IOException {
        // Given
        VocabularySet emptySet = new VocabularySet("German", "English");
        
        // When
        service.saveToFile(testFile.toPath(), emptySet);
        VocabularySet loadedSet = service.loadFromFile(testFile.toPath()).get();
        
        // Then
        assertNotNull(loadedSet, "Loaded set should not be null");
        assertEquals(0, loadedSet.getVocabularies().size(), "Set should not contain any vocabulary pairs");
        assertEquals("German", loadedSet.getSourceLanguage(), "Source language should match");
        assertEquals("English", loadedSet.getTargetLanguage(), "Target language should match");
    }
    
    @Test
    void shouldThrowExceptionWhenSaveToReadOnlyFile() throws IOException {
        // Given
        VocabularySet vocabularySet = new VocabularySet("English", "German");
        vocabularySet.addVocabularyPair(new VocabularyPair("house", "Haus"));
        
        // Create a file and make it read-only
        testFile.createNewFile();
        assertTrue(testFile.setReadOnly(), "Failed to set file as read-only");
        
        // Then
        assertThrows(RuntimeException.class, () -> {
            service.saveToFile(testFile.toPath(), vocabularySet);
        }, "Saving to a read-only file should throw an exception");
    }
}