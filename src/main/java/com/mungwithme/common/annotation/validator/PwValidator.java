package com.mungwithme.common.annotation.validator;

import com.mungwithme.common.annotation.valid.PwValid;
import com.mungwithme.common.util.RegexPatterns;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Slf4j
public class PwValidator implements ConstraintValidator<PwValid,String> {

    @Override
    public void initialize(PwValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // 빈값 message NotBlank 로 변경 함
        if (value == null || !StringUtils.hasText(value.trim())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("{error.NotBlank}")
                .addConstraintViolation();
            return false;
        }
        return Pattern.matches(RegexPatterns.PW_REGEX, value);
    }
}
