package com.mungwithme.marking.model.enums;

/**
 * 이 장소 LOCATION,
 * 이 동네 NEARBY
 *
 */
public enum SearchType {

    LOCATION("LOCATION"),
    NEARBY("NEARBY");

    private final String description;

    SearchType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static SearchType create(String requestType) {
        for (SearchType value : SearchType.values()) {
            if (value.description.equals(requestType)) {
                return value;
            }
        }
        return SearchType.NEARBY;
    }

}
