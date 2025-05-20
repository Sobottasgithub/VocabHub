package org.example.vocabhub.trainer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VocabularySet {
    private String sourceLanguage;
    private String targetLanguage;
    @JsonProperty
    private List<VocabularyPair> vocabularies = new ArrayList<>();

    public VocabularySet(String sourceLanguage, String targetLanguage) {
        this.sourceLanguage = sourceLanguage;
        this.targetLanguage = targetLanguage;
    }

    public VocabularySet() {
    }

    public List<VocabularyPair> getVocabularies() {
        return Collections.unmodifiableList(vocabularies);
    }

    public void setVocabularies(List<VocabularyPair> vocabularies) {
        this.vocabularies = vocabularies;
    }

    public boolean addVocabularyPair(VocabularyPair pair) {
        return this.vocabularies.add(pair);
    }

    public boolean removeVocabularyPair(VocabularyPair pair) {
        return this.vocabularies.remove(pair);
    }

    public String getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    @JsonIgnore
    public int getSize() {
        return this.vocabularies.size();
    }
}
