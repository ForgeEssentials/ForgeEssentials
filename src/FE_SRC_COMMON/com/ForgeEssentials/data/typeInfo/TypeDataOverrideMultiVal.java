package com.ForgeEssentials.data.typeInfo;

import java.rmi.server.UID;

import com.ForgeEssentials.api.data.ITypeInfo;

public abstract class TypeDataOverrideMultiVal<T> extends ITypeInfo<T>
{

	public TypeDataOverrideMultiVal(Class<? extends T> type)
	{
		super(type);
	}
	
	public TypeDataOverrideMultiVal(T obj)
	{
		super(obj);
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
	 * This method is called by the SQL dataDrivers in order to find out what sort of columns to create.
	 * As long as the classes are primitives, SaveableObjects, or are TypeOverriden, everything is good.
	 * having a NULL class is NOT a good idea.
	 * for Maps: this would return the key type and the value type.
	 * @return
	 */
	public abstract Class[] getSaveables();
	
	
	// ARGH!! need a way to send the data out...

}
