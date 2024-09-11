package com.mungwithme.common.file;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * 이미지 저장
 *
 * 
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStore {


    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    public static final String PET_DIR = "pet/";
    public static final String MARKING_DIR = "marking/";
    public static final String PROFILE_DIR = "profile/";

    /**
     * 이미지 List 로 업로드
     *
     * @param multipartFileList
     *   업로드 할 파일 리스트
     * @param pathType
     *   pathType (pet,profile,marking)
     */
    public List<String> uploadFiles(List<MultipartFile> multipartFileList, String pathType) throws IOException {
        // 이미지 파일만 업로드
        List<String> uploadFiles = new ArrayList<>();
        for (MultipartFile file : multipartFileList) {
            String saveName = uploadFile(file, pathType);
            uploadFiles.add(saveName);
        }
        return uploadFiles;
    }
    /**
     * 이미지 업로드
     *
     * @param multipartFile
     *     업로드 할 파일
     */
    /**
     *
     * @param multipartFile
     *   업로드 할 파일
     * @param pathType
     *   pathType (pet,profile,marking)
     *
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile multipartFile, String pathType) throws IOException {
        // 이미지 파일만 업로드
        FileUtils.validImgFile(multipartFile);

        // 확장자 jpeg 로 통일
        // 사진 이미지의 파일명은 전부다 랜덤아이디로
        String fileFormatName = ".jpeg";

        String folderPath = makeFolder(pathType);  // 날짜 폴더 생성

        String uuid = UUID.randomUUID().toString(); // UUID 생성

        String saveName = folderPath + File.separator + uuid + "_" + multipartFile.getOriginalFilename() + fileFormatName;

        Path savePath = Paths.get(saveName);

        multipartFile.transferTo(savePath);

        return saveName;
    }

    /**
     * 디렉터리 생성
     * <p>
     * image/이미지 타입 (pet,profile,marking)/년도/월/일
     *
     * @param path
     *     type + token
     * @return
     */
    public String makeFolder(String path) {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        String folderPath = str.replace("/", File.separator);

        String fullRoot = uploadPath + File.separator + path + folderPath;
        Path directoryPath = Paths.get(fullRoot);

        try {
            Files.createDirectories(directoryPath);
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return fullRoot;
    }


}
