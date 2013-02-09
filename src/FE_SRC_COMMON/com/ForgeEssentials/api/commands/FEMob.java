package com.ForgeEssentials.api.commands;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
public @interface FEMob
{
	EnumMobType type() default EnumMobType.HOSTILE;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(
	{ ElementType.METHOD, ElementType.FIELD })
	public @interface IsTamed
	{
	}
}
