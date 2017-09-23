package com.ai.aif.cache.voltdb.core.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented    //记录在javadoc中
@Retention(RetentionPolicy.RUNTIME)  //保存在JVM中
@Target(ElementType.FIELD)  //注解字段
public @interface AIColumn
{
	String name() default "";
}
