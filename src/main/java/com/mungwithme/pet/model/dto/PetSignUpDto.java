package com.mungwithme.pet.model.dto;

import com.mungwithme.pet.model.Breed;
import com.mungwithme.pet.model.Personality;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Setter
@Getter
public class PetSignUpDto {
    private Long userId;
    private String name;
    private List<Personality> personalities;
    private String description;
    private MultipartFile profile;
    private Breed breed;
}
