package com.mungwithme.common.file;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 확장자 검증
 */
public class FileUtils {
    private static final Tika tika = new Tika();
    public static final String[] IMAGE_EXT_LIST = {"image/jpg", "image/jpeg", "image/pjpeg", "image/png",
        "image/bmp", "image/x-windows-bmp"};


    /**
     *
     * 이미지 확장자 검증
     *
     * @param multipartFile
     * @return
     */
    public static boolean validImgFile(MultipartFile multipartFile) {
        List<String> notValidTypeList = Arrays.asList(IMAGE_EXT_LIST);
        return valid(multipartFile, notValidTypeList);
    }


    private static boolean valid(MultipartFile multipartFile, List<String> notValidTypeList) {
        try {
            String mimeType = tika.detect(multipartFile.getInputStream());
            return notValidTypeList.stream()
                .anyMatch(notValidType -> notValidType.equalsIgnoreCase(mimeType));
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
