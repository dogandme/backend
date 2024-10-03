package com.mungwithme.common.annotation.valid;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mungwithme.common.annotation.validator.MultipartImageValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
@Target({FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = MultipartImageValidator.class)
public @interface MultipartImageValid {

    String message() default "{picture.error}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
