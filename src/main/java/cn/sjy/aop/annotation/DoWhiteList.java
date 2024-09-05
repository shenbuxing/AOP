package cn.sjy.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)//@Retention的作用是定义注解的保留期限，在运行时保留，在编译器生成class文件时保留，在jvm加载class文件时保留。
@Target(ElementType.METHOD)
public @interface DoWhiteList {
    String key() default "";
    String returnJson() default "";
}
