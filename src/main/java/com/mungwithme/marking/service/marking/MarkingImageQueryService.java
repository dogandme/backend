package com.mungwithme.marking.service.marking;


import com.mungwithme.marking.model.entity.MarkImage;
import com.mungwithme.marking.repository.markImge.MarkImageQueryRepository;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MarkingImageQueryService {


    private final MarkImageQueryRepository markImageQueryRepository;

    public List<MarkImage> findAllByMarkingIds(Set<Long> markingIds) {
        return markImageQueryRepository.findAllByMarkingIds(markingIds);
    }

}
