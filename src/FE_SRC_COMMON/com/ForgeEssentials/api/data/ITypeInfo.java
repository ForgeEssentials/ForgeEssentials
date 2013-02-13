package com.ForgeEssentials.api.data;

import java.util.HashMap;


/**
 * The constructor for a class that implements this should take either the class or nothing.
 * The class taken in the constructor should be compatible with the parameter.
 * A class implementing that should ONLY have 1 constructor, the constructor will be used to instantiate via reflection.
 * Having more than one constructor may result in the wrong one being used.
 * @author AbrarSyed
 * @param <T> The class this will be the TypeOverride for.
 */
public interface ITypeInfo<T>
{
	/**
	 * If this class should be saved inLine with a class that has this as a field.
	 * If true, the UniqueKey will be saved, and will reference the class by its unique key elsewhere.
	 * @return
	 */
	public boolean canSaveInline();
	
	/**
	 * Should populate the given map with FieldNames and their types.
	 * These don't necessarily have to be the real names,
	 * as long as the same names are used to reconstruct the object
	 * a well as used to create the TypeData
	 */
	public void build(HashMap<String, Class> map);

	/**
	 * This should return a fully populated TypeData instance.
	 * This instance may be a specially created implementation, or be obtained via the DataStorageManager.
	 * DON'T FORGET THE UNIQUE KEY!
	 */
	 public TypeData getTypeDataFromObject(T obj);

	/**
	 * This method will be used to create an object from
	 * @param data If this class has a TypeDataOverride, you can cast it.
	 * @return a reconstructed Object
	 */
	 public T reconstruct(IReconstructData data);
	 
	 /**
	  * @return The class object this specific TypeInfo is representing.
	  */
	 public Class<? extends T> getType();
}
