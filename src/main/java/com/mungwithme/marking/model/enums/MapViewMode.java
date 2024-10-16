package com.mungwithme.marking.model.enums;

public enum MapViewMode {

    ALL_VIEW("ALL_VIEW"),
    CURRENT_LOCATION_CENTER("CURRENT_LOCATION"),
    MAP_LOCATION_CENTER("MAP_LOCATION");

    private final String description;

    MapViewMode(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
