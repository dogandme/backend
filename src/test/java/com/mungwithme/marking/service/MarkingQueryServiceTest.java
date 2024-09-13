package com.mungwithme.marking.service;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import java.util.List;
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

        Marking byId = markingQueryService.findById(21, false, true);

        Set<MarkImage> images = byId.getImages();

        for (MarkImage image : images) {
            System.out.println("image = " + image.getImageUrl());
        }
    }


    @Test
    public void findMarkingInBounds() {
        //현재 위도 좌표 (y 좌표)
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;
        List<Marking> markingInBounds = markingQueryService.findMarkingInBounds(southBottomLat, northTopLat,
            southLeftLng, northRightLng, false, false);
        for (Marking marking : markingInBounds) {
            System.out.println(" ====================================== " );
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getLat() = " + marking.getLat());
            System.out.println("marking.getLng() = " + marking.getLng());
            System.out.println("marking.getContent() = " + marking.getContent());
            System.out.println(" ====================================== " );
        }

    }
}