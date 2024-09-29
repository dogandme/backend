package com.mungwithme.common.annotation.validator;

import com.mungwithme.common.file.FileUtils;
import com.mungwithme.common.annotation.valid.MultipartImageValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

/**
 * image 확장자 검증 어노테이션
 *
 */
public class MultipartImageValidator implements ConstraintValidator<MultipartImageValid, MultipartFile> {

    @Override
    public void initialize(MultipartImageValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null) {
            // 기본메세지 제거
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("{file.error.NotBlank}")
                .addConstraintViolation();
            return false;
        }
        return FileUtils.validImgFile(value);
    }
}
