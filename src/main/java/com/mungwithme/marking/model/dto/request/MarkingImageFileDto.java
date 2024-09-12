package com.mungwithme.marking.model.dto.request;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class MarkingImageFileDto {
    private Integer order;
    private MultipartFile image;
}
