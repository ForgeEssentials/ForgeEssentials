package com.ForgeEssentials.data;

import java.lang.annotation.*;

/**
 * Marks a field within a class as saveable by the Data API.
 * One field per @SaveableObject should have the objectLoadingField set to true - this
 * will be used by the API as the 'uniqueLoadingKey' which must be some piece of data
 * that identifies the object uniquely.
 * 
 * @author MysteriousAges
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface SaveableField
{
	boolean uniqueLoadingField() default false;
	boolean nullableField() default false;
}
