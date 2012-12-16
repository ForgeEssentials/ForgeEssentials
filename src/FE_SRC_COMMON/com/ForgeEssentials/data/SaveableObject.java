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
    
    
	
    /**
     *This method must have exactly 1 argument, a TaggedClass.
     *When an object is loaded, this method will be called with a TaggedClass populated with all the read data.
     *In this method, the object should be constructed and registered as necessary.
     *This method may NOT be inherited
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Reconstructor {}
}
