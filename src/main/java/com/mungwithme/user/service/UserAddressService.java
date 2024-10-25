package com.mungwithme.user.service;

import com.mungwithme.common.jdbc.repository.JdbcRepository;
import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.user.model.entity.User;
import com.mungwithme.user.model.entity.UserAddress;
import com.mungwithme.user.repository.UserAddressRepository;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserAddressService implements JdbcRepository<UserAddress> {


    private final UserAddressRepository userAddressRepository;
    private final JdbcTemplate jdbcTemplate;


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



    @Override
    @Transactional
    public void saveAll(List<UserAddress> entityList, LocalDateTime createdDateTime) {
        jdbcTemplate.batchUpdate(
            "INSERT INTO user_address "
                + "(address_id,user_id,reg_Dt,mod_dt) "
                + "VALUES (?,?,?,?)",
            entityList, 50,
            (PreparedStatement ps, UserAddress userAddress) -> {
                ps.setLong(1, userAddress.getAddress().getId());
                ps.setLong(2, userAddress.getUser().getId());
                ps.setTimestamp(3, Timestamp.valueOf(createdDateTime));
                ps.setTimestamp(4, Timestamp.valueOf(createdDateTime));
            });
    }


    @Override
    public void save(UserAddress entity) {
        userAddressRepository.save(entity);
    }

    @Override
    public void delete(UserAddress entity) {
        userAddressRepository.delete(entity);

    }

    @Override
    public void deleteAll(UserAddress entity) {
    }
}
