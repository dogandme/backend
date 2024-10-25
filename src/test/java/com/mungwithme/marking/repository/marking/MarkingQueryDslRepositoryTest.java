package com.mungwithme.marking.repository.marking;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.maps.dto.response.LocationBoundsDto;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
import com.mungwithme.marking.model.dto.response.MarkRepDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.enums.MapViewMode;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import com.mungwithme.user.service.UserService;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@SpringBootTest
class MarkingQueryDslRepositoryTest {

    @Autowired
    MarkingQueryDslRepository markingQueryDslRepository;

    @Autowired
    UserQueryService userQueryService;

    @Autowired
    AddressQueryService addressQueryService;


    @Test
    public void findMarkByBounds() {

        // given
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;

        LocationBoundsDto locationBoundsDto = new LocationBoundsDto();
//        User user = null;
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
        locationBoundsDto.setNorthRightLng(northRightLng);
        locationBoundsDto.setNorthTopLat(northTopLat);
        locationBoundsDto.setSouthBottomLat(southBottomLat);
        locationBoundsDto.setSouthLeftLng(southLeftLng);

        List<MarkRepDto> markByBounds = markingQueryDslRepository.findMarksByBound(locationBoundsDto, user);

        System.out.println("markByBounds.size() = " + markByBounds.size());


    }

    @Test
    public void findCountBySubDistrict() {

        // given
        double northTopLat = 35.545047500080756;
        //현재 경도 좌표 (x 좌표)
        double northRightLng = 129.3521825968079;

        //현재 위도 좌표 (y 좌표)
        double southBottomLat = 35.520204401760736;
        //현재 경도 좌표 (x 좌표)
        double southLeftLng = 129.32615169340926;

        LocationBoundsDto locationBoundsDto = new LocationBoundsDto();
//        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);
        User user = null;

        locationBoundsDto.setNorthRightLng(northRightLng);
        locationBoundsDto.setNorthTopLat(northTopLat);
        locationBoundsDto.setSouthBottomLat(southBottomLat);
        locationBoundsDto.setSouthLeftLng(southLeftLng);

        Set<Address> addressSet = addressQueryService.findAddressInBounds(locationBoundsDto.getSouthBottomLat(),
            locationBoundsDto.getNorthTopLat(),
            locationBoundsDto.getSouthLeftLng(), locationBoundsDto.getNorthRightLng());

        Address address = addressQueryService.findById(1).orElse(null);
        Address address2 = addressQueryService.findById(2).orElse(null);
        Address address1 = addressQueryService.findById(3).orElse(null);

        addressSet.add(address1);
        addressSet.add(address);
        addressSet.add(address2);

        markingQueryDslRepository.findCountBySubDistrict(user,
            addressSet);

        // then

    }

    @Test
    public void findAllMarkersByUserDesc() {


    }
}