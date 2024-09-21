package com.mungwithme.pet.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PetInfoResponseDto {
    private Long petId;
    private String name;
    private String profile;
}
