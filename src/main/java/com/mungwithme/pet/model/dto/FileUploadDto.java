package com.mungwithme.pet.model.dto;

import lombok.Getter;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Getter
public class FileUploadDto {
    private String fileName;    // 업로드된 파일의 원래 이름
    private String fileData;    // 파일
    private String uuid;        // 파일의 UUID 값
    private String path;        // 업로드된 파일의 저장 경로

    public String getImageUrl() {
        return URLEncoder.encode(path + "/" + uuid + "_" + fileName, StandardCharsets.UTF_8);
    }
}
