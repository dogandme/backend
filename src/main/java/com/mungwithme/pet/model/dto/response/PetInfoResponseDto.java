package com.mungwithme.pet.model.dto.response;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PetInfoResponseDto {
    private Long petId;
    private String name;
    private String profile;
}
