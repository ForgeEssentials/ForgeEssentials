package com.ForgeEssentials.data;

import java.lang.annotation.*;

/**
 * Marks an object as saveable by the Data API. In order to be useful, it should contain at least
 * one field marked with the @SavedField annotation.
 * 
 * @author MysteriousAges
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SaveableObject
{
	/**
	 * Set to True if the object has data that can be included directly in a containing
	 * class's data file.
	 */
	boolean SaveInline() default false;
}
