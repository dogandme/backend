package com.mungwithme.pet.controller;

import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.pet.model.Personality;
import com.mungwithme.pet.service.PetService;
import com.mungwithme.pet.model.dto.PetSignUpDto;
import com.mungwithme.user.model.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/pets")
public class PetController {

    private final PetService petService;
    private final BaseResponse baseResponse;

    /**
     * 회원가입 3단계 : [일반/소셜] 애완동물 정보 저장
     * @param petSignUpDto 애완동물정보
     */
    @PostMapping("")
    public CommonBaseResult signUp3(@ModelAttribute PetSignUpDto petSignUpDto) throws Exception {

        HashMap<String, Object> result = new HashMap<>();

        try {
            User user = petService.signUp3(petSignUpDto); // 추가 정보 저장

            result.put("role", user.getRole().getKey());
            return baseResponse.getContentResult(result);
        } catch (ResourceNotFoundException e) {
            return baseResponse.getFailResult(404, "회원을 찾을 수 없습니다.");
        } catch (Exception e) {
            log.error(e.getMessage());
            return baseResponse.getFailResult(400, "error");
        }
    }

}
