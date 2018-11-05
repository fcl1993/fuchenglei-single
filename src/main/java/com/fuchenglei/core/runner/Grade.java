package com.fuchenglei.core.runner;

import java.lang.annotation.*;

@Inherited
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Grade
{

    //取最大
    int value() default Integer.MAX_VALUE;

}
