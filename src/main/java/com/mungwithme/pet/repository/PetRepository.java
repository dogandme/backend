package com.mungwithme.pet.repository;

import com.mungwithme.pet.model.entity.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet, Long> {
}
