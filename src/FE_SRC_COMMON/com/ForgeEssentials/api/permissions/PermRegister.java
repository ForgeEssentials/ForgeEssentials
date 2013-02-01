package com.ForgeEssentials.api.permissions;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation to register permissions.
 * @author AbrarSyed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface PermRegister
{
	/**
	 * Should be some String that identifies this permission registerer.
	 */
	String ident();
}
