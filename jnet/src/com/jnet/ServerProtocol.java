package com.jnet;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ServerProtocol
{
    int port() default 50000;
    String ip() default "";
    String name() default "unnamed";
    boolean pool() default false;
    int maxClients() default 5;
}
