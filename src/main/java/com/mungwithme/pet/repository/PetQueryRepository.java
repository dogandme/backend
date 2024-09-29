package com.mungwithme.pet.repository;

import com.mungwithme.pet.model.dto.response.PetInfoResponseDto;
import com.mungwithme.pet.model.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface PetQueryRepository extends JpaRepository<Pet, Long> {
    Pet findByUserId(Long userId);
}
