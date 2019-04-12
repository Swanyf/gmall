package com.swan.gmall.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequest {
    // 加在controller方法上用于判断哪些方法进入拦截器需要通过验证
    //    // 哪些放在未通过验证也能放行
    boolean isNeedLogin() default true;

}
