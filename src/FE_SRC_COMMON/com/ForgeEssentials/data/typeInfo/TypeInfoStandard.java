package com.ForgeEssentials.data.typeInfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.util.OutputHandler;

/**
 * This is the standard TypeInfo class for all classes that don't have the override.
 * The Build Method will throw an exception if its not correct.
 * @author AbrarSyed
 * @param <T> This would be set to Object, but subclasses may want to have TypeInfo's for very specific classes.
 */
public class TypeInfoStandard<T> implements ITypeInfo<T>
{
	Class<? extends T>	type;
	private boolean		isUniqueKeyField;
	private boolean		inLine;
	private String		uniqueKey;
	private String		reconstructorMethod;
	private String[]	fields;

	public TypeInfoStandard(Class<? extends T> type)
	{
		this.type = type;
	}

	@Override
	public void build(HashMap<String, Class> map)
	{
		SaveableObject AObj = (SaveableObject) type.getAnnotation(SaveableObject.class);
		inLine = AObj.SaveInline();

		Class currentType = type;
		Annotation a;

		// Iterate through this class and superclass's and get saveable fields
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				// if its a saveable field
				if (f.isAnnotationPresent(SaveableField.class))
					map.put(f.getName(), f.getType());

				// check for UniqueKey
				if (f.isAnnotationPresent(UniqueLoadingKey.class))
				{
					if (uniqueKey != null)
						throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");
					if (!f.getType().isPrimitive() && !f.getType().equals(String.class))
						throw new RuntimeException("The UniqueLoadingKey must be a primitive or a string");

					isUniqueKeyField = true;
					uniqueKey = f.getName();
				}

			}
		} while ((currentType = currentType.getSuperclass()) != null);

		// find reconstructor method
		for (Method m : type.getDeclaredMethods())
		{
			// catches the Reconsutructor
			if (m.isAnnotationPresent(Reconstructor.class))
			{
				if (reconstructorMethod != null)
					throw new RuntimeException("Each class may only have 1 reconstructor method");
				if (!Modifier.isStatic(m.getModifiers()))
					throw new RuntimeException("The reconstructor method must be static!");
				if (!m.getReturnType().equals(type))
					throw new RuntimeException("The reconstructor method must return " + type);
				if (!Arrays.equals(m.getParameterTypes(), new Class[] { IReconstructData.class }))
					throw new RuntimeException("The reconstructor method must have exactly 1 " + IReconstructData.class + "paremeter!");

				reconstructorMethod = m.getName();
			}
			// catches the UniqueLoadingKey method variant
			else if (m.isAnnotationPresent(UniqueLoadingKey.class))
			{
				if (uniqueKey != null)
					throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");

				if (m.getParameterTypes().length > 0)
					new RuntimeException("The reconstructor method must have no paremeters");

				if (!m.getReturnType().isPrimitive() && !m.getReturnType().equals(String.class))
					throw new RuntimeException("The UniqueLoadingKey method must return a primitive or a string");

				uniqueKey = m.getName();
				isUniqueKeyField = false;
			}
		}

		// unsures that the FieldToType stuff is saved here.
		fields = map.keySet().toArray(new String[map.size()]);
	}

	@Override
	public TypeData getTypeDataFromObject(T objectSaved)
	{
		Class c = objectSaved.getClass();
		TypeData data = DataStorageManager.getDataForType(type);
		Field f;
		Object obj;

		// do Unique key stuff
		try
		{
			if (isUniqueKeyField)
			{
				f = c.getDeclaredField(uniqueKey);
				f.setAccessible(true);
				data.setUniqueKey(f.get(objectSaved).toString());
			}
			else
			{
				Method m;
				String methodName = uniqueKey.replace("()", "");
				m = c.getDeclaredMethod(methodName, new Class[] {});
				m.setAccessible(true);
				Object val = m.invoke(objectSaved, new Object[] {});
				data.setUniqueKey(val.toString());

			}
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Reflection error trying to get UniqueLoadingKey from " + objectSaved.getClass() + ". FE will continue without saving this.", e);
		}

		Class currentClass = c;
		// Iterate over the object grabbing the fields we want to examine.
		for (int i = 0; i < fields.length; ++i)
		{
			try
			{
				f = currentClass.getDeclaredField(fields[i]);
				f.setAccessible(true);
				obj = f.get(objectSaved);

				if (obj != null)
				{
					if (isTypeComplex(obj))
					{
						// This object is not a primitive. Call this function on the appropriate TypeTagger.
						obj = DataStorageManager.getInfoForType(obj.getClass()).getTypeDataFromObject(obj);
					}
					data.putField(fields[i], obj);
				}
				// Ensure we reset the currentClass after trying this. It may have been altered by a previous attempt.
				currentClass = c;
			}
			catch (NoSuchFieldException e)
			{
				// Try again with a parent class.
				currentClass = currentClass.getSuperclass();
				if (currentClass == null)
				{
					// Unless this happens. (Note: This shouldn't happen.)
					OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.", e);
				}
				--i;
			}
			catch (Exception e)
			{
				// This... Should not happen. Unless something stupid.
				OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.", e);
			}
		}

		return data;
	}

	@Override
	public T reconstruct(IReconstructData data)
	{
		try
		{
			Method reconstructor = type.getDeclaredMethod(reconstructorMethod, IReconstructData.class);
			reconstructor.setAccessible(true);
			return (T) reconstructor.invoke(null, data);
		}
		catch (Throwable thrown)
		{
			OutputHandler.exception(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
		}

		return null;
	}

	@Override
	public boolean canSaveInline()
	{
		return inLine;
	}

	@Override
	public Class<? extends T> getType()
	{
		return type;
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
