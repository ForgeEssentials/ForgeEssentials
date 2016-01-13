package com.forgeessentials.core.preloader.asminjector.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.forgeessentials.core.preloader.asminjector.InjectionPoint;

/**
 * Annotation for injections
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject
{

    /**
     * Target method name and descriptor. <br>
     * Example: <code>myMethod(IILjava/lang/String;)V</code>
     * 
     * @return target method name for this injector
     */
    public String target();

    /**
     * Whether to completely overwrite the target method or not
     * 
     * @return flag specifying, if this injector should overwrite the target method completely
     */
    public boolean overwrite() default false;

    /**
     * Array of {@link At} annotations which describe the {@link InjectionPoint}s in the target method.
     * 
     * @return injection point specifiers for this injector
     */
    public At[] at();

    /**
     * List of <code>key=value</code> pairs that are used for obfuscation mapping
     * 
     * @return alias mappings
     */
    public String[] aliases() default {};

    /**
     * Priority of this injector. Default priority is 0 (can be negative). A higher value means the injector is executed earlier.
     * 
     * @return
     */
    public int priority() default 0;

}
