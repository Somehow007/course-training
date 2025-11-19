package com.ujs.trainingprogram.tp.authentication;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 鉴权注解，判断用户是否有权限操作
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequireAuthentication {

    /**
     * 权限等级，判断权限是否足够
     */
    String value();
}
