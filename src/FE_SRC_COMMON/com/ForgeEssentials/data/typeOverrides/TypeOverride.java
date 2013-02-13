package com.ForgeEssentials.data.typeOverrides;

import com.ForgeEssentials.data.TaggedClass;

public abstract class TypeOverride<T> extends TaggedClass
{
	T object;

	/**
	 * This should be extended by all classes.
	 * It will be used to create a TaggedClass from this object.
	 * @param object
	 */
	public TypeOverride(T object)
	{
		super(object.getClass());
		this.object = object;
	}
	
	/**
	 * This should be extended by all classes.
	 * It will be used to create a TaggedClass from this object.
	 * @param object
	 */
	public TypeOverride(Class<? extends T> type)
	{
		super(type);
	}
	
	/**
	 * This will be called after creating this with the taggedClass.
	 * @return the reconstructed Object
	 */
	public abstract T reconstruct();
	
	/**
	 * Fills the taggedClass maps with SavedFields.
	 */
	public abstract void build();
}
