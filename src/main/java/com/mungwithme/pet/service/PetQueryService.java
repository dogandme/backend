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
     *  유저의 펫 조회
     * @param user 유저
     * @return
     */
    public Optional<Pet> findByUser(User user) {
        return petQueryRepository.findByUser(user);
    }
}
