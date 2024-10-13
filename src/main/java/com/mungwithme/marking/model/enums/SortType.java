package com.mungwithme.marking.model.enums;

public enum SortType {
    POPULARITY("POPULARITY"),
    RECENT("RECENT"),
    DISTANCE("DISTANCE");

    private final String description;

    SortType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
