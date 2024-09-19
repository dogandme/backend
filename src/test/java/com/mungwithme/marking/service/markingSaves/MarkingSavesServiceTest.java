package com.mungwithme.marking.service.markingSaves;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarkingSavesServiceTest {


    @Autowired
    MarkingSavesService markingSavesService;

    @Test
    public void addSaves() {

        // given
        markingSavesService.addSaves(16);

        // when

        // then

    }



    @Test
    public void deleteSaves() {

        // given
        markingSavesService.deleteSaves(16);

        // when

        // then

    }
}