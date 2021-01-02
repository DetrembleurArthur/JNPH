package com.jnet;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClientProtocol
{
    int port() default 50000;
    String ip() default "127.0.0.1";
    String name() default "unnamed";
}
