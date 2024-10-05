package com.mungwithme.user.service;

import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserAddress;
import com.mungwithme.user.repository.UserAddressRepository;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAddressService {


    private final UserAddressRepository userAddressRepository;


    @Transactional
    public void removeAllByUser(User user) {
        userAddressRepository.deleteAllByUser(user);

    }

    @Transactional
    public void removeSet(Set<UserAddress> userAddresses) {
        userAddressRepository.deleteAllInBatch(userAddresses);

    }

    @Transactional
    public void addSet(Set<UserAddress> userAddresses) {
        userAddressRepository.saveAll(userAddresses);

    }
}
