package com.mungwithme.pet.model.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class PetInfoResponseDto {
    private Long petId;
    private String name;
    private String description;
    private String profile;
    private String breed;
    private List<String> personalities;
}
