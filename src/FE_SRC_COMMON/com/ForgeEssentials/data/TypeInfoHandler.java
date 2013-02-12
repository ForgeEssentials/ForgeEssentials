package com.ForgeEssentials.data;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.util.OutputHandler;

/**
 * Responsible for transforming a particular type (class) into a TaggedClass for
 * easier storage via DataDrivers, and eventually converts a TaggedClass into
 * the original Object (More or less.)
 * @author MysteriousAges
 */
public abstract class TypeInfoHandler
{
	private TypeInfoHandler(){}
	
	protected static TypeInfoHandler getTaggerForType(Class c)
	{
		TypeInfoHandler tagger;

		// if the Annotaton exists
		if (c.isAnnotationPresent(SaveableObject.class))
		{
			// build it the standard way.
			tagger = new TypeInfoHandler(c, false);
			HashMap<String, Class> map = new HashMap<String, Class>();
			tagger.build(map);
			return tagger;
		}

		// arrays prolly won't have the annotation...
		if (c.isArray())
		{
			// array.. build it the Non-Standard way.
		}

		// check for other registerred types...

		return null;
	}


	/**
	 * Reocnstructs the object from its TaggedClass
	 * @param data
	 * @return
	 */
	public Object createFromFields(TypeData data)
	{
		Object val;
		for (Entry<String, Object> entry : data.getAllFields())
		{
			if (entry.getValue() instanceof IReconstructData)
			{
				val = DataStorageManager.getInfoForType(getTypeOfField(entry.getKey())).reconstruct((IReconstructData) entry.getValue());
				data.putField(entry.getKey(), val);
			}
		}

		try
		{
			if (isCustom)
				val = ((ITypeInfo) this).reconstruct(data);
			else
			{
				Method reconstructor = type.getDeclaredMethod(reconstructorMethod, IReconstructData.class);
				reconstructor.setAccessible(true);
				val = reconstructor.invoke(null, data);
			}
			data.putField(entry.getKey(), val);
		}
		catch (Throwable thrown)
		{
			OutputHandler.felog.log(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
		}

		return value;
	}

	/**
	 * @param t Type of object to check
	 * @return True if TypeTagger must create a nested TaggedClass to allow
	 *         DataDrivers to correctly save the object.
	 */
	public static boolean isTypeComplex(Object obj)
	{
		boolean flag = true;

		if (obj.getClass().isPrimitive() || obj instanceof Integer || obj instanceof int[] || obj instanceof Float || obj instanceof Double || obj instanceof double[] || obj instanceof Boolean || obj instanceof boolean[] || obj instanceof String || obj instanceof String[])
		{
			flag = false;
		}

		return flag;
	}

	/**
	 * @param t class check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save this type of object.
	 */
	public static boolean isTypeComplex(Class obj)
	{
		boolean flag = true;
		if (obj.isPrimitive() || obj.equals(Integer.class) || obj.equals(int[].class) || obj.equals(Float.class) || obj.equals(Double.class) || obj.equals(double[].class) || obj.equals(Boolean.class) || obj.equals(boolean[].class)
				|| obj.equals(String.class) || obj.equals(String[].class))
		{
			flag = false;
		}

		return flag;
	}
}
