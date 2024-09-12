package com.mungwithme.marking.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarkingQueryServiceTest {

    @Autowired
    MarkingQueryService markingQueryService;
    @Test
    void findById() {

        Marking byId = markingQueryService.findById(1, false, false);

        Set<MarkImage> images = byId.getImages();

        for (MarkImage image : images) {
            System.out.println("image = " + image.getImageUrl());
        }

    }
}