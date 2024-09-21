package com.mungwithme.marking.service.marking;

import com.mungwithme.maps.dto.response.LocationBoundsDTO;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoWithLikedResponseDto;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MarkingSearchServiceTest {

    @Autowired
    MarkingSearchService markingSearchService;

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

        LocationBoundsDTO locationBoundsDTO = new LocationBoundsDTO(southBottomLat, northTopLat, southLeftLng,
            northRightLng);

        try {
            List<MarkingInfoResponseDto> nearbyMarkers = markingSearchService.findNearbyMarkers(locationBoundsDTO);
            System.out.println("nearbyMarkers.size() = " + nearbyMarkers.size());
        }catch (Exception e) {

            System.out.println("e.getMessage() = " + e.getMessage());
        }


    }

    @Test
    public void findAllLikedMarkersByUser() {

        List<MarkingInfoResponseDto> allLikedMarkersByUser = markingSearchService.findAllLikedMarkersByUser();

        for (MarkingInfoResponseDto markingInfoResponseDto : allLikedMarkersByUser) {
            MarkingInfoWithLikedResponseDto markingInfoWithLikedResponseDto = (MarkingInfoWithLikedResponseDto) markingInfoResponseDto;
            System.out.println(markingInfoWithLikedResponseDto.getMarkingId());

            System.out.println("markingInfoWithLikedResponseDto.getLikedId() = " + markingInfoWithLikedResponseDto.getLikedId());

            System.out.println("markingInfoWithLikedResponseDto.getRegDt() = " + markingInfoWithLikedResponseDto.getLikedRegDt());

        }




    }
}