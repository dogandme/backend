package com.mungwithme.marking.repository.marking;

import static org.junit.jupiter.api.Assertions.*;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.service.AddressQueryService;
import com.mungwithme.marking.model.dto.request.MarkingSearchDto;
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
    public void findAllMarkersByUserDesc() {
        double lng = 129.3149;
        double lat = 35.45662;

        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        MarkingSearchDto markingSearchDto = new MarkingSearchDto();

        markingSearchDto.setLat(lat);
        markingSearchDto.setLng(lng);

        Set<Long> addressIds = new HashSet<>();
        addressIds.add(1L);
        addressIds.add(2L);

        List<Address> addresses = addressQueryService.findByIds(addressIds);

        Page<MarkingQueryDto> page = markingQueryDslRepository.findAllMarkersByUser(
            new HashSet<>(addresses)
            , markingSearchDto,
            false,
            false,
            user,
            user,
            of,
            SortType.DISTANCE, MapViewMode.ALL_VIEW, false);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println(" ========================================= ");
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);
            System.out.println(" ========================================= ");

        }


    }
}