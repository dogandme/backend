package com.mungwithme.marking.model.dto.sql;


import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.pet.model.entity.Pet;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MarkingQueryDto {
    private Marking marking;

    private Pet pet;


    /**
     * *equals**와 hashCode 메서드를 올바르게 구현하면, Set 자료구조에서 중복된 DTO 객체들이 제거됩니다.
     * @param o
     * @return
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MarkingQueryDto that = (MarkingQueryDto) o;
        return Objects.equals(marking, that.marking) && Objects.equals(pet, that.pet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(marking, pet);
    }
}
