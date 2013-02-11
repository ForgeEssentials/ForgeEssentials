package com.ForgeEssentials.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.AbstractTypeInfo;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.AbstractTypeData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SavedField;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.OutputHandler;

/**
 * Responsible for transforming a particular type (class) into a TaggedClass for
 * easier storage via DataDrivers, and eventually converts a TaggedClass into
 * the original Object (More or less.)
 * @author MysteriousAges
 */
public class TypeInfo implements ITypeInfo
{
	public static TypeInfo getTaggerForType(Class c)
	{
		TypeInfo tagger;

		// if the Annotaton exists
		if (c.isAnnotationPresent(SaveableObject.class))
		{
			// build it the standard way.
			tagger = new TypeInfo(c, false);
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

	public final boolean				isCustom;
	protected Class						type;
	protected boolean					isUniqueKeyField;
	protected boolean					inLine;
	protected String					uniqueKey;
	protected String					reconstructorMethod;
	protected HashMap<String, Class>	fieldToTypeMap;

	// this is just a dummy. this should be taken from the static method above.
	protected TypeInfo(Class type, boolean isCustom)
	{
		type = this.type;
		this.isCustom = isCustom;
	}

	protected void build(Map<String, Class> map)
	{
		SaveableObject AObj = (SaveableObject) type.getAnnotation(SaveableObject.class);
		inLine = AObj.SaveInline();

		Class currentType = type;
		map = new HashMap<String, Class>();
		Annotation a;

		// Iterate through this class and superclass's and get saveable fields
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				// if its a saveable field
				if (f.isAnnotationPresent(SaveableField.class))
				{
					// check for UniqueKey
					if (f.isAnnotationPresent(UniqueLoadingKey.class))
					{
						assert uniqueKey == null : new RuntimeException("Each class may only have 1 UniqueLoadingKey");
						assert f.getType().isPrimitive() || f.getType().equals(String.class) : new RuntimeException("The UniqueLoadingKey must be a primitive or a string");
						isUniqueKeyField = true;
						uniqueKey = f.getName();
					}

					map.put(f.getName(), f.getType());
				}

				else if (f.isAnnotationPresent(UniqueLoadingKey.class))
					throw new RuntimeException("if the UniqueLoadingKey is to be a field, it must be a SaveableField as well");
			}
		} while ((currentType = currentType.getSuperclass()) != null);

		// find reconstructor method
		for (Method m : type.getDeclaredMethods())
		{
			// catches the Reconsutructor
			if (m.isAnnotationPresent(Reconstructor.class))
			{
				assert reconstructorMethod == null : new RuntimeException("Each class may only have 1 reconstructor method");
				assert Modifier.isStatic(m.getModifiers()) : new RuntimeException("The reconstructor method must be static!");
				assert m.getReturnType().equals(type) : new RuntimeException("The reconstructor method must return " + type);
				assert m.getParameterTypes().length == 1 : new RuntimeException("The reconstructor method must have exactly 1 paremeter/argument");
				assert m.getParameterTypes()[0].equals(IReconstructData.class) : new RuntimeException("The reconstructor method must have a " + IReconstructData.class + " parameter");

				reconstructorMethod = m.getName();
			}
			// catches the UniqueLoadingKey methiod variant
			else if (m.isAnnotationPresent(UniqueLoadingKey.class))
			{
				assert uniqueKey == null : new RuntimeException("Each class may only have 1 UniqueLoadingKey");
				assert m.getParameterTypes().length == 0 : new RuntimeException("The reconstructor method must have no paremeters");
				assert (m.getReturnType().isPrimitive() && m.getReturnType().equals(Void.class)) || m.getReturnType().equals(String.class) : new RuntimeException("The UniqueLoadingKey method must return a primitive or a string");

				uniqueKey = m.getName();
				isUniqueKeyField = false;
			}
		}
	}

	public HashMap<String, Class> getFieldToTypeMap()
	{
		return fieldToTypeMap;
	}

	public Class getTypeOfField(String fieldName)
	{
		return fieldToTypeMap.get(fieldName);
	}

	/**
	 * COnstructs a tagged class from the given object.
	 * @param objectSaved
	 * @return
	 */
	public AbstractTypeData getTypeDataFromObject(Object objectSaved)
	{
		Class c = objectSaved.getClass();
		TypeData data = (TypeData) TypeData.getTaggedClass(c);
		Field f;
		Object obj;

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
			OutputHandler.severe("Reflection error trying to get UniqueLoadingKey from " + objectSaved.getClass() + ". FE will continue without saving this.");
			e.printStackTrace();
		}

		Class currentClass = c;
		String[] savedFields = fieldToTypeMap.keySet().toArray(new String[] {});
		// Iterate over the object grabbing the fields we want to examine.
		for (int i = 0; i < savedFields.length; ++i)
		{
			try
			{
				f = currentClass.getDeclaredField(savedFields[i]);
				f.setAccessible(true);
				obj = f.get(objectSaved);

				if (obj != null)
				{
					if (TypeInfo.isTypeComplex(obj))
					{
						// This object is not a primitive. Call this function on
						// the appropriate TypeTagger.
						obj = DataStorageManager.getTaggerForType(obj.getClass()).getTypeDataFromObject(obj);
					}
					data.addField(new SavedField(savedFields[i], obj));
				}
				// Ensure we reset the currentClass after trying this. It may
				// have been altered by a previous attempt.
				currentClass = c;
			}
			catch (NoSuchFieldException e)
			{
				// Try again with a parent class.
				currentClass = currentClass.getSuperclass();
				if (currentClass == null)
				{
					// Unless this happens. (Note: This shouldn't happen.)
					OutputHandler.info("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
					e.printStackTrace();
				}
				--i;
			}
			catch (Throwable e)
			{
				// This... Should not happen. Unless something stupid.
				OutputHandler.info("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * Reocnstructs the object from its TaggedClass
	 * @param data
	 * @return
	 */
	public Object createFromFields(TypeData data)
	{
		Object value = null;
		for (SavedField field : data.TaggedMembers.values())
		{
			if (field.value instanceof IReconstructData)
			{
				field.value = DataStorageManager.getTaggerForType(getTypeOfField(field.name)).createFromFields((TypeData) field.value);
			}
		}

		try
		{
			if (isCustom)
				value = ((ITypeInfo) this).reconstruct(data);
			else
			{
				Method reconstructor = type.getDeclaredMethod(reconstructorMethod, IReconstructData.class);
				reconstructor.setAccessible(true);
				value = reconstructor.invoke(null, data);
			}
		}
		catch (Throwable thrown)
		{
			OutputHandler.felog.log(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
		}

		return value;
	}

	/**
	 * @param t
	 *            Type of object to check
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

	@Override
	public Object reconstruct(IReconstructData data)
	{
		try
		{
			Method reconstructor = type.getDeclaredMethod(reconstructorMethod, IReconstructData.class);
			reconstructor.setAccessible(true);
			return reconstructor.invoke(null, data);
		}
		catch (Throwable thrown)
		{
			OutputHandler.exception(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
		}

		return null;
	}
}
