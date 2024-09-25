package com.mungwithme.marking.controller;


import com.mungwithme.common.exception.ResourceNotFoundException;
import com.mungwithme.common.file.FileStore;
import com.mungwithme.common.response.BaseResponse;
import com.mungwithme.common.response.CommonBaseResult;
import com.mungwithme.marking.model.dto.request.MarkingAddDto;
import com.mungwithme.marking.model.dto.request.MarkingModifyDto;
import com.mungwithme.marking.model.dto.request.MarkingRemoveDto;
import com.mungwithme.marking.model.dto.response.MarkingInfoResponseDto;
import com.mungwithme.marking.service.marking.MarkingService;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 마킹 CRUD controller
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/markings")
public class MarkingController {

    private final MarkingService markingService;
    private final BaseResponse baseResponse;
    private final FileStore fileStore;

    /**
     * marking 저장 API
     *
     * @param markingAddDto
     * @param images
     * @return
     */
    @PostMapping
    public ResponseEntity<CommonBaseResult> saveMarkingWithImages(
        @Validated @RequestPart(name = "markingAddDto") MarkingAddDto markingAddDto,
        @RequestPart(name = "images") List<MultipartFile> images, HttpServletRequest request) throws IOException {
        markingService.addMarking(markingAddDto, images, false);

        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "marking.save.success", request.getLocale());

    }

    /**
     * marking 수정 API
     *
     * @param markingModifyDto
     * @param images
     * @return
     */
    @PutMapping
    public ResponseEntity<CommonBaseResult> modifyMarking(
        @Validated @RequestPart(name = "markingModifyDto") MarkingModifyDto markingModifyDto,
        @RequestPart(name = "images", required = false) List<MultipartFile> images, HttpServletRequest request)
        throws IOException {
        // TODO postMan 테스트를 위해 임시
        if (images == null) {
            images = new ArrayList<>();
        }
        markingService.patchMarking(markingModifyDto, images, false);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "modify.success", request.getLocale());


    }

    /**
     * marking 삭제 API
     */
    @DeleteMapping
    public ResponseEntity<CommonBaseResult> removeMarking(@Validated @RequestBody MarkingRemoveDto markingRemoveDto,
        HttpServletRequest request)
        throws IOException {
        markingService.deleteMarking(markingRemoveDto, false);
        return baseResponse.sendSuccessResponse(HttpStatus.OK.value(), "marking.remove.success", request.getLocale());
    }


    /**
     * 마킹 상세 정보 반환 API
     *
     * @param id
     *     마킹 아이디
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonBaseResult> fetchMarkingById(@PathVariable(name = "id") Long id) throws IOException {
        MarkingInfoResponseDto markingInfoResponseDto = markingService.fetchMarkingInfoResponseDto(id, false,
            false);
        return baseResponse.sendContentResponse(markingInfoResponseDto, HttpStatus.OK.value());

    }


    /**
     * id:user_file_Api_1
     * <p>
     * 서버에서 마킹 이미지 가져오기
     *
     * @return
     * @throws IOException
     */
    @GetMapping("/image/{fileName}")
    public ResponseEntity<UrlResource> fetchMarkingImage(@PathVariable(name = "fileName") String fileName) {
        // file MediaType 확인 후 header 에 저장
        MediaType mediaType = null;
        UrlResource pictureImage = null;
        try {
            if (StringUtils.hasText(fileName)) {
                mediaType = MediaType.parseMediaType(Files.probeContentType(Paths.get(fileName)));
                pictureImage = fileStore.getUrlResource(fileName, FileStore.MARKING_DIR);
            }
        } catch (Exception e) {
            throw new ResourceNotFoundException("error.notfound.image");
        }
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, mediaType.toString())
            .body(pictureImage);
    }

}
