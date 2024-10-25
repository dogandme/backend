package com.mungwithme.marking.repository.marking;

import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@SpringBootTest
class MarkingQueryRepositoryTest {


    @Autowired
    MarkingQueryRepository markingQueryRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UserQueryService userQueryService;
    @Test
    void findNearbyMarkers_v2() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;

        Address address = addressRepository.findById(3630L).orElse(null);

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Long> addressSet = new HashSet<>();

        addressSet.add(address.getId());
        addressSet.add(2L);
        addressSet.add(3L);


    }


    @Test
    void findNearbyMarkers_v4() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;

        Address address = addressRepository.findById(3630L).orElse(null);

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);


    }


    @Test
    void findMarkersOrderByLikesDesc() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;


        Address address = addressRepository.findById(3630L).orElse(null);
        Address address1 = addressRepository.findById(2L).orElse(null);

        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);
        addressSet.add(address1);

        Page<MarkingQueryDto> page = markingQueryRepository.findMarkersOrderByLikesDesc(lat,lng,addressSet, false,
            false, user, of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();

            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);

            System.out.println("likeCount = " + likeCount);

        }

    }

    @Test
    void findMarkersOrderByRecentDesc() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;


        Address address = addressRepository.findById(3630L).orElse(null);
        Address address1 = addressRepository.findById(2L).orElse(null);

        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);
        addressSet.add(address1);

        Page<MarkingQueryDto> page = markingQueryRepository.findMarkersOrderByRegDtDesc(lat,lng,addressSet, false,
            false, user, of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);

        }

    }


    @Test
    void findMarkersOrderByDistAsc() throws IOException {

        double lng = 129.3149;
        double lat = 35.45662;


        Address address = addressRepository.findById(3630L).orElse(null);
        Address address1 = addressRepository.findById(2L).orElse(null);

        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("22213251@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);
        addressSet.add(address1);

        Page<MarkingQueryDto> page = markingQueryRepository.findMarkersOrderByDistAsc(lat,lng,addressSet, false,
            false, user, of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);

        }

    }


    @Test
    void findAllMarkersByUserRegDtDesc() throws IOException {

        double lng = 129.3149;
        double lat = 35.45662;



        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Page<MarkingQueryDto> page = markingQueryRepository.findAllMarkersByUserRegDtDesc(lat,lng, false,
            false, user.getId(), of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println(" ========================================= " );
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);
            System.out.println(" ========================================= " );

        }
    }

    @Test
    void findAllMarkersByUserLikesDesc() throws IOException {

        double lng = 129.3149;
        double lat = 35.45662;



        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Page<MarkingQueryDto> page = markingQueryRepository.findAllMarkersByUserLikesDesc(lat,lng, false,
            false, user.getId(), of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println(" ========================================= " );
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);
            System.out.println(" ========================================= " );

        }
    }


    @Test
    void findAllMarkersByUserDistAsc() throws IOException {

        double lng = 129.3149;
        double lat = 35.45662;



        PageRequest of = PageRequest.of(0, 20);
        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Page<MarkingQueryDto> page = markingQueryRepository.findAllMarkersByUserDistAsc(lat,lng, false,
            false, user.getId(), of);

        List<MarkingQueryDto> content = page.getContent();
        for (MarkingQueryDto markingQueryDto : content) {

            Marking marking = markingQueryDto.getMarking();
            long likeCount = markingQueryDto.getLikeCount();
            long saveCount = markingQueryDto.getSaveCount();
            double distance = markingQueryDto.getDistance();
            System.out.println(" ========================================= " );
            System.out.println("marking.getId() = " + marking.getId());
            System.out.println("marking.getRegDt() = " + marking.getRegDt());
            System.out.println("saveCount = " + saveCount);
            System.out.println("distance = " + distance);
            System.out.println("likeCount = " + likeCount);
            System.out.println(" ========================================= " );

        }
    }

    @Test
    void findNearbyMarkers_v3() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;

        Address address = addressRepository.findById(3630L).orElse(null);

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Long> addressSet = new HashSet<>();

        addressSet.add(address.getId());


    }




}