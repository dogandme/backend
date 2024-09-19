package com.mungwithme.marking.model;


import com.mungwithme.common.exception.ResourceNotFoundException;

public enum Visibility {
    PUBLIC("PUBLIC"),
    FOLLOWERS_ONLY("FOLLOWERS_ONLY"),
    PRIVATE("PRIVATE");

    private final String description;

    Visibility(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // 예: 문자열로부터 해당 enum 값을 가져오는 메서드
    public static Visibility fromDescription(String description) {
        for (Visibility visibility : Visibility.values()) {
            if (visibility.getDescription().equals(description)) {
                return visibility;
            }
        }
        throw new ResourceNotFoundException("해당 설명에 맞는 공개 범위가 없습니다: " + description);
    }

}
