package com.neusoft.health.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOperation {

    String module() default "";

    String operation() default "";

    String targetType() default "";

    String targetIdParam() default "";
}
