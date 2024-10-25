package com.mungwithme.marking.model.enums;

public enum MapViewMode {

    ALL_VIEW("ALL_VIEW"),
    CURRENT_LOCATION("CURRENT_LOCATION"),
    MAP_LOCATION("MAP_LOCATION");

    private final String description;

    MapViewMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static MapViewMode create(String requestMode) {
        for (MapViewMode value : MapViewMode.values()) {
            if (value.description.equals(requestMode)) {
                return value;
            }
        }
        return MapViewMode.ALL_VIEW;
    }
}
