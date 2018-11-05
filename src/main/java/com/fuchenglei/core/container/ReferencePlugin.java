package com.fuchenglei.core.container;

import java.lang.annotation.*;

@Target(value = ElementType.TYPE)
@Retention(value = RetentionPolicy.RUNTIME)
@Plugin
@Inherited
public @interface ReferencePlugin
{
}
