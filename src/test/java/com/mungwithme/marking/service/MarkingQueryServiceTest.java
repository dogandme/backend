package com.mungwithme.marking.service;

import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.marking.service.marking.MarkingQueryService;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
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
    UserQueryService userQueryService;

    @Test
    void findById() {

        Marking byId = markingQueryService.findById(21, false, true);

        Set<MarkImage> images = (Set<MarkImage>) byId.getImages();

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
    public void findAllMarkersByUser() {

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

    /**
     * 위 예시들을 통해 정리해보면 연관관계의 주인이 호출할 때는 지연 로딩이 정상적으로 동작하지만,
     * 연관관계의 주인이 아닌 곳에서 호출한다면 지연 로딩이 아닌 즉시 로딩으로 동작한다는 것을 알 수 있다.
     */
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
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        System.out.println("체크!");

        Set<MarkingQueryDto> nearbyMarkers = markingQueryRepository.findNearbyMarkers(southBottomLat,
            northTopLat,
            southLeftLng, northRightLng, false, false, user);

        for (MarkingQueryDto markingQueryDto : nearbyMarkers) {

            Marking marking = markingQueryDto.getMarking();
            Pet pet = markingQueryDto.getPet();
            System.out.println("=============================");

            System.out.println("nearbyMarker.getId() = " + marking.getId());

            System.out.println("nearbyMarker.getContent() = " + marking.getContent());

            System.out.println("marking.getUser().getPet().getName() = " + pet.getName());

            System.out.println("nearbyMarker.getCountData().getSavedCount() = " + marking.getSaves().size());

        }


    }

    @Test
    public void findNearbyMarkers3() {

//        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
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
//        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
//        markingQueryRepository.findNearbyMarkers(false, false, user);
    }


    @Test
    public void findAllLikedMarkersByUser() {

        // given
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
        // when

        Set<MarkingQueryDto> allLikedMarkersByUser = markingQueryService.findAllLikedMarkersByUser(user, false, false);


        // then
        System.out.println("allLikedMarkersByUser.size() = " + allLikedMarkersByUser.size());

        for (MarkingQueryDto markingQueryDto : allLikedMarkersByUser) {
            System.out.println("markingQueryDto.getLikes().getId() = " + markingQueryDto.getMarkingLikes().getId());

        }


    }

    @Test
    public void findAllSavedMarkersByUser() {

        // given
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
        // when

        Set<MarkingQueryDto> allLikedMarkersByUser = markingQueryService.findAllSavedMarkersByUser(user, false, false);


        // then
        System.out.println("allLikedMarkersByUser.size() = " + allLikedMarkersByUser.size());

        for (MarkingQueryDto markingQueryDto : allLikedMarkersByUser) {
            System.out.println(" ==============================  ");
            System.out.println("마킹saveId = " + markingQueryDto.getMarkingSaves().getId());
            System.out.println("마킹아이디 = " + markingQueryDto.getMarkingSaves().getMarking().getId());
            System.out.println("markingQueryDto.getMarking().getIsVisible() = " + markingQueryDto.getMarking().getIsVisible());
            System.out.println("saveCount = " + markingQueryDto.getMarking().getSaves().size());

        }
    }



    @Test
    public void findAll() {
        User user = userQueryService.findByEmail("lim642666@gmail.com").orElse(null);

        Set<Marking> all = markingQueryService.findAll(user, false);



    }
}