package com.ForgeEssentials.data.typeOverrides;

import java.rmi.server.UID;

public abstract class TypeOverrideBase<T>
{
	Class<? extends T>	type;
	T					object;

	public TypeOverrideBase(T object)
	{
		this.object = object;
	}

	/**
	 * Should return a String of length no greater than 60
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
	 * This should not be a full class name, nor any longer than 20 characters.
	 * Should not contain any spaces, quotes, or slashes. Preferably an
	 * abbreviation of the type.
	 * 
	 * @return
	 */
	public abstract String getTypeName();
}
