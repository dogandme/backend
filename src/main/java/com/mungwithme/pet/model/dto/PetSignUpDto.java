package com.mungwithme.pet.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class PetSignUpDto {
    private String name;
    private List<String> personalities;
    private String description;
    private String breed;
}
