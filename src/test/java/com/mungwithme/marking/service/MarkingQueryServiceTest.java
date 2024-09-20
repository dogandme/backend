package com.mungwithme.marking.service;

import com.mungwithme.marking.model.dto.request.MarkingTestDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserService;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarkingQueryServiceTest {

    @Autowired
    MarkingQueryService markingQueryService;
    @Autowired
    MarkingQueryRepository markingQueryRepository;
    @Autowired
    UserService userService;

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
            System.out.println(" ====================================== ");
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getLat() = " + marking.getLat());
            System.out.println("marking.getLng() = " + marking.getLng());
            System.out.println("marking.getContent() = " + marking.getContent());
            System.out.println(" ====================================== ");
        }

    }


    @Test
    public void findNearbyMarkers() {

        //현재 위도 좌표 (y 좌표)
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;

        User user = userService.findByEmail("lim642666@gmail.co").orElse(null);
        List<MarkingInfoResponseDto> nearbyMarkers = markingQueryRepository.findNearbyMarkers(southBottomLat,
            northTopLat,
            southLeftLng, northRightLng, false, false, user);
//        for (Marking marking : markingInBounds) {
//            System.out.println(" ====================================== ");
//            System.out.println("marking.getId() = " + marking.getId());
//            System.out.println("marking.getLat() = " + marking.getLat());
//            System.out.println("marking.getLng() = " + marking.getLng());
//            System.out.println("marking.getContent() = " + marking.getContent());
//            System.out.println("marking.getIsVisible() = " + marking.getIsVisible());
//
//            System.out.println("marking.getUser().getPets() = " + marking.getUser().getPet());
//            System.out.println(" ====================================== ");
//        }

    }

    @Test
    public void findNearbyMarkers2() {

        //현재 위도 좌표 (y 좌표)
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;
        User user = userService.findByEmail("2221325@naver.com").orElse(null);
        List<MarkingInfoResponseDto> nearbyMarkers = markingQueryRepository.findNearbyMarkers(southBottomLat,
            northTopLat,
            southLeftLng, northRightLng, false, false, user);

        for (MarkingInfoResponseDto nearbyMarker : nearbyMarkers) {

            System.out.println("=============================");

            System.out.println("nearbyMarker.getId() = " + nearbyMarker.getId());

            System.out.println("nearbyMarker.getContent() = " + nearbyMarker.getContent());

            System.out.println("nearbyMarker.getNickName() = " + nearbyMarker.getNickName());

            System.out.println("nearbyMarker.getIsOwner() = " + nearbyMarker.getIsOwner());

            System.out.println("nearbyMarker.getCountData().getSavedCount() = " + nearbyMarker.getCountData().getSavedCount());

        }


    }

    @Test
    public void findNearbyMarkers3() {


//        User user = userService.findByEmail("2221325@naver.com").orElse(null);
//        List<MarkingTestDto> nearbyMarkers = markingQueryRepository.findNearbyMarkers(false, false, user);
//        for (MarkingTestDto dto  : nearbyMarkers) {
//            Marking marking = dto.getMarking();
//            System.out.println(" ====================================== ");
//            System.out.println("marking.getId() = " + marking.getId());
//            System.out.println("marking.getUser().getId() = " + marking.getUser().getId());
//
//            System.out.println("marking.getLat() = " + marking.getLat());
//            System.out.println("marking.getLng() = " + marking.getLng());
//            System.out.println("marking.getContent() = " + marking.getContent());
//            System.out.println("marking.getIsVisible() = " + marking.getIsVisible());
//
//            for (MarkImage image : marking.getImages()) {
//                System.out.println("image.getImageUrl() = " + image.getImageUrl());
//            }
//            System.out.println("marking.getLikedCount() = " + dto.getLikedCount());
//
//            System.out.println(" ====================================== ");
//        }
    }



    @Test
    public void findNearbyMarkers4() {
//
//        User user = userService.findByEmail("2221325@naver.com").orElse(null);
//        markingQueryRepository.findNearbyMarkers(false, false, user);

    }
}