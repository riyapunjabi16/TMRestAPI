package com.thinking.machines.tmws.annotations;
import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
public @interface OnException
{
public String handler();
public boolean isPublic();
}
