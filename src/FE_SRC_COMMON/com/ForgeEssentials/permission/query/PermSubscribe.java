package com.ForgeEssentials.permission.query;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import com.ForgeEssentials.permission.query.PermQuery.PermResult;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.EnumSet;

import net.minecraftforge.event.EventPriority;

@Retention(value = RUNTIME)
@Target(value = METHOD)
public @interface PermSubscribe
{
	public EventPriority priority() default EventPriority.NORMAL;
	public PermResult[] handleResult() default {PermResult.ALLOW, PermResult.DENY, PermResult.PARTIAL, PermResult.UNKNOWN};
}
