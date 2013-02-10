package com.ForgeEssentials.data.typeOverrides;

import java.rmi.server.UID;

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
	 * Should return a String of length no greater than 80
	 * 
	 * @return
	 */
	public final String getNewUID()
	{
		UID id = new UID();
		String last = getTypeName() + "_-_" + id;
		last.replace(" ", "-");
		return last;
	}

	/**
	 * This should not be a full class name, nor any longer than 30 characters.
	 * Should not contain any spaces, quotes, or slashes.
	 * Preferably an abbreviation of the type.
	 */
	public abstract String getTypeName();
	
	/**
	 * This will be called after creating this with the taggedClass.
	 * @return the reconstructed Object
	 */
	public abstract T reconstruct();
}
