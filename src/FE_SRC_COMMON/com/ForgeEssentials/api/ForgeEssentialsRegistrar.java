package com.ForgeEssentials.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to mark classes where static methods with other FE annotations might be.
 * @author AbrarSyed
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface ForgeEssentialsRegistrar
{
	String ident();

	/**
	 * Called before Pre-Init
	 * @param event IPermRegisterEvent
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ ElementType.METHOD })
	public @interface PermRegister
	{
	}
}
