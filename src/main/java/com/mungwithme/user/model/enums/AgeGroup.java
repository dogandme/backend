package com.mungwithme.user.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AgeGroup {
    TEENAGER(10),
    TWENTIES(20),
    THIRTIES(30),
    FORTIES(40),
    FIFTIES(50),
    SIXTIES_AND_ABOVE(60);

    private final int age;

    AgeGroup(int age) {
        this.age = age;
    }

    //JSON 으로 변환할 때 age 필드를 사용하도록 설정했습니다. 즉, JSON 응답에서는 10, 20 같은 숫자 값으로 변환됩니다.
    @JsonValue
    public int getAge() {
        return age;
    }

    // @JsonCreator: JSON 요청으로부터 숫자 값이 들어왔을 때, 해당 값에 맞는 AgeGroup을 찾습니다.
    @JsonCreator
    public static AgeGroup fromValue(int age) {
        for (AgeGroup ageGroup : AgeGroup.values()) {
            if (ageGroup.age == age) {
                return ageGroup;
            }
        }
        throw new IllegalArgumentException("error.arg");
    }
}
