package com.ForgeEssentials.data.typeData;

import java.rmi.server.UID;

import com.ForgeEssentials.data.TypeData;

public abstract class TypeDataOverride<T> extends TypeData
{
	T object;

	/**
	 * This should be extended by all classes.
	 * It will be used to create a TaggedClass from this object.
	 * @param object
	 */
	public TypeDataOverride(T object)
	{
		super(object.getClass());
		this.object = object;
	}
	
	/**
	 * This should be extended by all classes.
	 * It will be used to create a TaggedClass from this object.
	 * @param object
	 */
	public TypeDataOverride(Class<? extends T> type)
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
