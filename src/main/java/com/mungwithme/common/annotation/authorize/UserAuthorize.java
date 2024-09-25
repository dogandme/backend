package com.mungwithme.common.annotation.authorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * 유저가 접근가능한 곳은 운영자도 접근 가능
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)

@PreAuthorize("hasAnyRole('USER','ADMIN')")
public @interface UserAuthorize {

}
