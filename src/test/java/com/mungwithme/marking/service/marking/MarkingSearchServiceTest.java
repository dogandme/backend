package com.mungwithme.marking.service.marking;

import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingLikedInfoResponseDto;
import com.mungwithme.marking.model.dto.response.MarkingPagingResponseDto;
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




    }

    @Test
    public void findAllLikedMarkersByUser() {

//        List<MarkingInfoResponseDto> allLikedMarkersByUser = markingSearchService.findAllLikedMarkersByUser();



    }
}