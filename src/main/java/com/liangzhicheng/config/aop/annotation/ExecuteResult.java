package com.liangzhicheng.config.aop.annotation;

import java.lang.annotation.*;

@Documented
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExecuteResult {

    String key(); //业务执行key

}