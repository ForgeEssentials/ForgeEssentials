package com.forgeessentials.core.preloader.asminjector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for injector class classes
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Mixin
{

    /**
     * Target class(es) for this injector class
     *
     * @return target classes for this injector class
     */
    public Class<?>[] value();

    /**
     * Target class name(es) for this injector class
     *
     * @return target classes for this injector class
     */
    public String[] targets() default {};

}
