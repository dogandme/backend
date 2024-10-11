package com.mungwithme.marking.repository.marking;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mungwithme.address.model.entity.Address;
import com.mungwithme.address.repository.AddressRepository;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.pet.model.entity.Pet;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.service.UserQueryService;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.yaml.snakeyaml.error.Mark;

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

        Set<Object[]> nearbyMarkers_v2 = markingQueryRepository.findNearbyMarkers_v2(addressSet, false,
            false, user.getId());

        for (Object[] objects : nearbyMarkers_v2) {

            System.out.println(" ================================== ");
            for (Object object : objects) {
                System.out.println("object = " + object);

            }

            

        }


    }


    @Test
    void findNearbyMarkers_v4() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;

        Address address = addressRepository.findById(3630L).orElse(null);

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);

        Set<Marking> nearbyMarkers_v4 = markingQueryRepository.findNearbyMarkers_v4(addressSet, false,
            false, user);

        System.out.println("nearbyMarkers_v4.size() = " + nearbyMarkers_v4.size());

    }


    @Test
    void findNearbyMarkers_v5() throws IOException {

        double lng = 129.3149;
        double lat = 35.55662;

        Address address = addressRepository.findById(3630L).orElse(null);

        User user = userQueryService.findByEmail("2221325@naver.com").orElse(null);

        Set<Address> addressSet = new HashSet<>();

        addressSet.add(address);

        Set<Object[]> nearbyMarkers_v5 = markingQueryRepository.findNearbyMarkers_v5(addressSet, false,
            false, user);

        for (Object[] objects : nearbyMarkers_v5) {
            Marking marking = (Marking) objects[0];
            Object object2 = objects[1];
            Object object3 = objects[2];
            System.out.println("object1 = " + marking.getImages().size());
            System.out.println("object2 = " + object2);
            System.out.println("object3 = " + object3);


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
        List<Object[]> results = markingQueryRepository.findNearbyMarkers_v2(lat, lng, addressSet, false, false,  user.getId());

    }




}