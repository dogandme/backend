package com.mungwithme.pet.repository;

import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PetQueryRepository extends JpaRepository<Pet, Long> {

    Pet findByUserId(Long userId);

    @Query("select p from Pet p where p.user=:user")
    Optional<Pet> findByUser(@Param("user") User user);
}
