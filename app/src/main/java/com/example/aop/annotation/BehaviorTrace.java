package com.example.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * author: xpf
 * time: 2020/1/29 21:33
 * describe: 用来表示性能检测的注解
 */
@Target(METHOD)
@Retention(RUNTIME)
public @interface BehaviorTrace {
    String value();
}
