package com.mungwithme.pet.model.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class PetRequestDto {
    private String name;
    private List<String> personalities;
    private String description;
    private String breed;
    private String profile;
}
