package com.forgeessentials.core.preloader.asminjector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Shadow
{

    /**
     * Aliases for this shadowed field
     */
    public String[] value() default {};

}
