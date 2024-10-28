package com.mungwithme.common.file;


import com.mungwithme.common.exception.ResourceNotFoundException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 저장
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FileStore {


    @Value("${com.example.ex8_fileupload.upload.path}") // application 의 properties 의 변수
    private String uploadPath;

    public static final String PET_DIR = "pet";
    public static final String MARKING_DIR = "marking";
    public static final String PREVIEW_DIR = "preview";
    public static final String USER_PROFILE_DIR = "profile";
    public static final String IMAGE_EXT = "webp";

    public static final String USER_DEFAULT_DIR = "default";


    /**
     * 이미지 파일 다중 업로드 API
     *
     * @param multipartFileList
     *     업로드 할 파일 리스트
     * @param pathType
     *     pathType (pet,profile,marking)
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
     * @param multipartFile
     *     업로드 할 파일
     * @param pathType
     *     pathType (pet,profile,marking)
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile multipartFile, String pathType) throws IOException {

        return uploadFile(multipartFile, pathType, UUID.randomUUID().toString());
    }

    /**
     * @param multipartFile
     *     업로드 할 파일
     * @param pathType
     *     pathType (pet,profile,marking)
     * @return
     * @throws IOException
     */
    public String uploadFile(MultipartFile multipartFile, String pathType, String uuid) throws IOException {
        // 이미지 파일만 업로드
        FileUtils.validImgFile(multipartFile);

        // 확장자 jpeg 로 통일
        // 사진 이미지의 파일명은 전부다 랜덤아이디로

        String folderPath = makeFolder(pathType);  // 날짜 폴더 생성

        String storeName = uuid + "." + IMAGE_EXT;

        String saveName = folderPath + File.separator + storeName;

        Path savePath = Paths.get(saveName);

        multipartFile.transferTo(savePath);

        return uuid;
    }


    /**
     * 마커용 프리뷰 이미지 생성
     *
     * @param targetWidth
     *     변경할 사이즈
     * @param multipartFile
     *     파일
     * @param pathType
     *     폴더
     * @param previewFileName
     *     파일 네임
     * @throws IOException
     */
    public void updatePreviewFile(int targetWidth,
        MultipartFile multipartFile,
        String pathType, String previewFileName) throws IOException {
        // 이미지 파일만 업로드
        FileUtils.validImgFile(multipartFile);

        // 확장자 jpeg 로 통일
        // 사진 이미지의 파일명은 전부다 랜덤아이디로
        String folderPath = makeFolder(pathType);  //  폴더 생성

        String storeName = previewFileName + "." + IMAGE_EXT;

        String saveName = folderPath + File.separator + storeName;

        MultipartFile resizeImage = resizeImage(targetWidth, multipartFile, "jpeg", storeName);

        resizeImage.transferTo(new File(saveName));
    }


    /**
     * 해당 디렉터리가 있는지 확인 후 없으면 생성
     * <p>
     * image/이미지 타입 (pet,profile,marking)/년도/월/일
     *
     * @param path
     *     type + token
     * @return
     */
    public String makeFolder(String path) {
        String folderPath = getFolderPath(path);
        Path directoryPath = Paths.get(folderPath);
        try {
            boolean isDirectory = Files.isDirectory(directoryPath);
            if (!isDirectory) { // 있는지 확인
                Files.createDirectories(directoryPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("error.internal");
        }
        return folderPath;
    }

    /**
     * 사진 삭제 API
     *
     * @param dirPath
     *     path type
     * @param filename
     *     fileName
     */
    public void deleteFile(String dirPath, String filename) {

        String folderPath = getFolderPath(dirPath);
        Path filePath = Paths.get(folderPath + File.separator + filename + "." + IMAGE_EXT);
        try {
            boolean isExecutable = Files.exists(filePath);
            if (isExecutable) { // 있는지 확인
                Files.delete(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("error.internal");
        }
    }


    /**
     * 이미지 resize
     *
     * @param targetWidth
     * @param originalFile
     * @param fileFormatName
     * @param storeFileName
     * @return
     * @throws IOException
     */
    public MultipartFile resizeImage(int targetWidth, MultipartFile originalFile, String fileFormatName,
        String storeFileName) throws IOException {

        // MultipartFile -> BufferedImage Convert
        BufferedImage image = null;
        try {
            image = ImageIO.read(originalFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        /**
         *
         // 작아도 500x500으로 유지할 예정으로 주석처리
         // newWidth : newHeight = originWidth : originHeight
         int originWidth = image.getWidth();
         int originHeight = image.getHeight();

         // 500 x 500 을 위한 주석처리
         int targetHeight = targetWidth * originHeight / originWidth;

         // 작아도 500x500으로 유지할 예정으로 주석처리
         // origin 이미지가 resizing 될 사이즈보다 작을 경우 resizing 작업 안함
         if (originWidth < targetWidth) {
         return originalFile;
         }
         */

        MarvinImage imageMarvin = new MarvinImage(image);

        Scale scale = new Scale();

        scale.load();
        scale.setAttribute("newWidth", targetWidth);
        scale.setAttribute("newHeight", targetWidth);
        scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

        BufferedImage imageNoAlpha = imageMarvin.getBufferedImageNoAlpha();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        ImageIO.write(imageNoAlpha, fileFormatName, baos);
        baos.flush();

        return new MockMultipartFile(storeFileName, baos.toByteArray());
    }


    /**
     * 폴더 및 폴더 내 파일 삭제 메서드
     *
     * @param dirPath
     *     path type
     */
    public void deleteFolder(String dirPath) {
        Path folderPath = Paths.get(getFolderPath(dirPath));
        try {
            if (Files.exists(folderPath)) {
                // Files.walkFileTree를 사용하여 폴더 내 모든 파일과 폴더를 삭제
                Files.walkFileTree(folderPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        // 파일 삭제
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        // 디렉토리 삭제
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException("error.internal", e);
        }
    }


    public MultipartFile convertUrlResourceToMultipartFile(String fileName, String dirPath) {
        // 1. UrlResource를 생성합니다.
        UrlResource urlResource = getUrlResource(fileName, dirPath);

        // 2. UrlResource에서 InputStream을 가져옵니다.
        try (InputStream inputStream = urlResource.getInputStream()) {
            // 3. MockMultipartFile을 사용해 InputStream을 MultipartFile로 변환합니다.
            return new MockMultipartFile(
                UUID.randomUUID().toString(),                               // 파일 이름
                urlResource.getFilename(),            // 원본 파일 이름
                "image/webp",           // 파일 타입 (필요에 따라 변경 가능)
                inputStream                           // 파일 내용 (InputStream)
            );
        } catch (Exception e) {
            throw new ResourceNotFoundException("error.notfound.image");
        }


    }


    /**
     * image URLResource 반환
     *
     * @param fileName
     *     이미지명
     * @param dirPath
     *     폴더타입 (marking,pet,profile)
     * @return
     */
    public UrlResource getUrlResource(String fileName, String dirPath) {
        UrlResource resource = null;
        try {
            log.info(
                "\"file:\" + uploadPath + File.separator + dirPath + File.separator + fileName + \".\" + IMAGE_EXT = {}",
                "file:" + uploadPath + File.separator + dirPath + File.separator + fileName + "." + IMAGE_EXT);

            resource = new UrlResource(
                "file:" + uploadPath + File.separator + dirPath + File.separator + fileName + "." + IMAGE_EXT);
        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("error.notfound.image");
        }
        return resource;
    }


    private String getFolderPath(String path) {
        return this.uploadPath + File.separator + path;
    }


}
