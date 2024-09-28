package com.mungwithme.common.annotation.valid;


import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.mungwithme.common.annotation.validator.PwValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

// 비밀번호 Valid 어노테이션
@Target({FIELD}) // 변수 위에 사용하는 어노테이션이기 때문에 Target은 FIELD로 설정해줍니다.
@Retention(RUNTIME) // 유지범위 설정
@Constraint(validatedBy = PwValidator.class)
@Documented
public @interface PwValid {

//    /**
//     * @return the regular expression to match
//     */
//    String regexp();

    /**
     * @return the error message template
     */
    String message() default "{error.arg.pw}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
