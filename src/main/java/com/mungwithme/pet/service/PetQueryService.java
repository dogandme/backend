package com.mungwithme.pet.service;

import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PetQueryService {

    private final PetQueryRepository petQueryRepository;

    /**
     * userId를 이용하여 펫 조회
     * @param userId 유저PK
     * @return 펫 정보
     */
    public PetInfoResponseDto findPetByUserId(Long userId) {
        Pet pet = petQueryRepository.findByUserId(userId);
        return pet == null ? null : PetInfoResponseDto.builder()
                .petId(pet.getId())
                .name(pet.getName())
                .description(pet.getDescription())
                .profile(pet.getProfile())
                .breed(pet.getBreed())
                .personalities(pet.getPersonalities())
                .build();
    }
}
