package com.mungwithme.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 * 사용자가 API 에 접근하기 전에 권한에 따른 인가 처리 어노테이션
 * method 에도 적용시킬수 있고
 * 클래스에도 적용시킬수 있다 클래스에 적용시킬경우 전체 메서드가 권한 제어가 된다.
 *
 *
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN')")
public @interface AdminAuthorize {

}
