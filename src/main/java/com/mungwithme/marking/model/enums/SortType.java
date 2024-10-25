package com.mungwithme.marking.model.enums;

import java.util.Locale.Category;

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
    public static SortType create(String requestSortType) {
        for (SortType value : SortType.values()) {
            if (value.toString().equals(requestSortType)) {
                return value;
            }
        }
        return SortType.RECENT;
    }
}
