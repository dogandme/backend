package com.mungwithme.marking.service.marking;


import com.mungwithme.address.model.entity.Address;
import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.model.dto.sql.MarkingQueryDto;
import com.mungwithme.marking.model.entity.Marking;
import com.mungwithme.marking.model.enums.SortType;
import com.mungwithme.marking.repository.marking.MarkingQueryRepository;
import com.mungwithme.user.model.entity.User;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingQueryService {


    private final MarkingQueryRepository markingQueryRepository;

    /**
     * 마킹 상세 정보 검색 API
     *
     * @param id
     *     마킹 고유값
     * @param isDeleted
     *     마킹 삭제 여부
     * @param isTempSaved
     *     마킹 임시 저장 여부
     * @return
     */
    public Marking findById(long id, boolean isDeleted, boolean isTempSaved) {
        return markingQueryRepository.findById(id, isDeleted, isTempSaved)
            .orElseThrow(() -> new ResourceNotFoundException("error.notfound.markings"));
    }

    /**
     * marking
     *
     */

    /**
     * marking 상세 정보 검색 API
     *
     * @param id
     *     marking 아이디
     * @param isDeleted
     *     삭제여부
     * @param isTempSaved
     *     임시저장 여부
     * @return
     */
    public MarkingInfoResponseDto findMarkingInfoDto(User user, long id, boolean isDeleted, boolean isTempSaved) {

        Marking findMarking = findById(id, isDeleted, isTempSaved);

        MarkingInfoResponseDto markingInfoResponseDto = new MarkingInfoResponseDto(findMarking);

        markingInfoResponseDto.updateIsOwner(user.getEmail().equals(findMarking.getUser().getEmail()));

        return markingInfoResponseDto;
    }








    /**
     * 내가 업로드한 마킹 리스트 (임시저장 포함)
     *
     * @param isDeleted
     * @param user
     * @return
     */
    public Set<Marking> findAll(
        User user,
        boolean isDeleted
    ) {
        return markingQueryRepository.findAll(user.getId(), isDeleted);
    }



    /**
     * 좌표값을 이용한 바운더리 계산
     *
     * @return
     */
    public List<Marking> findMarkingInBounds(
        double southBottomLat,
        double northTopLat,
        double southLeftLng,
        double northRightLng, boolean isDeleted, boolean isTempSaved) {
        return markingQueryRepository.findMarkingInBounds(
            southBottomLat, northTopLat, southLeftLng, northRightLng,
            isDeleted, isTempSaved);
    }


    /**
     * 유저의 마킹 수 조회
     *
     * @param userId
     *     유저PK
     * 유저의 임시 저장 마킹 수 조회
     * @param userId 유저PK
     * @return 마킹 수
     */
    public int countTempMarkingByUserIdAndIsTempSavedTrue(Long userId) {
        return markingQueryRepository.countTempMarkingByUserIdAndIsTempSavedTrue(userId);
    }


    public Set<Marking> findMarkingsByUser (boolean isDeleted,boolean isTempSaved,long userId) {
        return markingQueryRepository.findMarkingsByUser(isDeleted, isTempSaved, userId);
    }



}
