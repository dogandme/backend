package com.mungwithme.common.file;

import static org.mockito.Mockito.mock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;


@SpringBootTest
class FileStoreTest {


    @Autowired
    FileStore fileStore;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void uploadFile() throws IOException {

        // given
        File file = new File("/Users/imhaneul/Downloads/뉴진스/NJ_BubbleGum_21.jpg");
        FileInputStream inputStream = new FileInputStream(file);

        System.out.println("file.getName() = " + file.getName());

        // 실제 파일을 MultipartFile로 변환
        MultipartFile multipartFile = new MockMultipartFile(
            "file",
            file.getName(),
            "image/jpeg",
            inputStream
        );

        String s = fileStore.uploadFile(multipartFile,FileStore.PROFILE_DIR);

        System.out.println("s = " + s)
        ;
    }
    
    
    @Test
    public void createDir() {
    // given
    
    // when
        String dir = fileStore.makeFolder(FileStore.PET_DIR);

        System.out.println("File.separator = " + dir);


    // then
    
    }
}