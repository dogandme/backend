package com.mungwithme.marking.repository;


import com.mungwithme.marking.model.entity.Marking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarkingRepository extends JpaRepository<Marking,Long> {



    
}
