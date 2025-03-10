package org.example.vocabhub.utils;

public class VocableTableViewItem {
    private final String key;
    private final String value;

    public VocableTableViewItem(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}