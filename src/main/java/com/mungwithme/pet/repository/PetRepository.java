package com.mungwithme.pet.repository;

import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<Pet, Long> {


    @Query("select p from Pet p where p.user.id=:userId")
    Optional<Pet> findByUser(@Param("userId") long userId);

}
