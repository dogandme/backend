package com.mungwithme.pet.service;

import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.pet.repository.PetQueryRepository;
import com.mungwithme.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

    /**
     *  유저의 펫 조회
     * @param user 유저
     * @return
     */
    public Optional<Pet> findByUser(User user) {
        return petQueryRepository.findByUser(user);
    }
}
