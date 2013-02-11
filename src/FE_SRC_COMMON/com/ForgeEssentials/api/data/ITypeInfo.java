package com.ForgeEssentials.api.data;

import java.util.Map;


/**
 * The constructor for a class that implements this should take either the class or nothing.
 * the class should be the same as the paremeter.
 * @author AbrarSyed
 * @param <T> The class this will be the TypeOverride for.
 */
public interface ITypeInfo<T>
{
	/**
	 * Should populate the given map with FieldNames and their types.
	 * These don't necessarily have to be the real names,
	 * as long as the same names are used to reconstruct the object
	 * a well as used to create the TypeData
	 */
	public abstract void build(Map<String, Class> map);

	/**
	 * This should return a fully populated AbstractTypeData instance.
	 * This instance may be a specially created implementation, or be obtained via the DataStorageManager.
	 */
	public abstract AbstractTypeData getTypeDataFromObject(T objectSaved);

	/**
	 * This method will be used to create an object from
	 * @param data If this class has a TypeDataOverride, you can cast it.
	 * @return a reconstructed Object
	 */
	public abstract T reconstruct(IReconstructData data);
}
