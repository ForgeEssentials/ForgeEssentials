package com.ForgeEssentials.data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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
    
    /**
     * Marks a field within a class as saveable by the Data API.
     * @author MysteriousAges
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD})
    public @interface SaveableField
    {
    	boolean nullableField() default false;
    }
    
    /**
     * Marks a field or method as the UniqueLoadingKey.
     * There must be 1, and only 1 of these in each class. They are not inherited.
     * this must be/return some piece of data that identifies the object uniquely from others of its type.
     * @author AbrarSyed
     *
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD, ElementType.FIELD})
    public @interface UniqueLoadingKey{}
    
	
    /**
     *This method must have exactly 1 argument, a TaggedClass.
     *When an object is loaded, this method will be called with a TaggedClass populated with all the read data.
     *In this method, the object should be constructed and registered as necessary.
     *This method may NOT be inherited
     *@author AbrarSyed
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Reconstructor {}
}
