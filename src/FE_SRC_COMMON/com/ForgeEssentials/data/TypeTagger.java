package com.ForgeEssentials.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import com.ForgeEssentials.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.SaveableObject.SaveableField;
import com.ForgeEssentials.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.OutputHandler;

/**
 * Responsible for transforming a particular type (class) into a TaggedClass for easier storage via DataDrivers, and eventually converts a TaggedClass into the
 * original Object (More or less.)
 * 
 * @author MysteriousAges
 * 
 */
public class TypeTagger
{
	protected Class forType;
	protected boolean isUniqueKeyField;
	protected boolean inLine;
	protected String uniqueKey;
	protected String reconstructorMethod;
	protected String[] savedFields;
	protected HashMap<String, Class> fieldToTypeMap;

	public TypeTagger(Class type)
	{
		SaveableObject AObj = (SaveableObject) type.getAnnotation(SaveableObject.class);
		inLine = AObj.SaveInline();

		forType = type;
		Class currentType = forType;
		fieldToTypeMap = new HashMap<String, Class>();

		ArrayList<String> tempList = new ArrayList<String>();
		Annotation a;

		// Iterate through this class and superclass's and get saveable fields
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				if (f.isAnnotationPresent(SaveableField.class))
				{
					if (f.isAnnotationPresent(UniqueLoadingKey.class))
					{
						assert uniqueKey == null : new RuntimeException("Each class may only have 1 UniqueLoadingKey");
						assert f.getType().isPrimitive() || f.getType().equals(String.class) : new RuntimeException(
								"The UniqueLoadingKey must be a primitive or a string");
						isUniqueKeyField = true;
						uniqueKey = f.getName();
					}
					else
					{
						tempList.add(f.getName());
					}

					fieldToTypeMap.put(f.getName(), f.getType());
				}
				else if (f.isAnnotationPresent(UniqueLoadingKey.class))
				{
					throw new RuntimeException("if the UniqueLoadingKey is to be a field, it must be a SaveableField as well");
				}
			}
		}
		while ((currentType = currentType.getSuperclass()) != null);

		// find reconstructor method
		for (Method m : type.getDeclaredMethods())
		{
			if (m.isAnnotationPresent(Reconstructor.class))
			{
				assert reconstructorMethod == null : new RuntimeException("Each class may only have 1 reconstructor method");
				assert Modifier.isStatic(m.getModifiers()) : new RuntimeException("The reconstructor method must be static!");
				assert m.getReturnType().equals(type) : new RuntimeException("The reconstructor method must return " + type);
				assert m.getParameterTypes().length == 1 : new RuntimeException("The reconstructor method must have exactly 1 paremeter/argument");
				assert m.getParameterTypes()[0].equals(TaggedClass.class) : new RuntimeException("The reconstructor method must have a " + TaggedClass.class
						+ " parameter");

				reconstructorMethod = m.getName();
			}
			else if (m.isAnnotationPresent(UniqueLoadingKey.class))
			{
				assert uniqueKey == null : new RuntimeException("Each class may only have 1 UniqueLoadingKey");
				assert m.getParameterTypes().length == 0 : new RuntimeException("The reconstructor method must have no paremeters");
				assert (m.getReturnType().isPrimitive() && m.getReturnType().equals(Void.class)) || m.getReturnType().equals(String.class) : new RuntimeException(
						"The UniqueLoadingKey method must return a primitive or a string");

				uniqueKey = m.getName();
				isUniqueKeyField = false;
			}
		}

		savedFields = tempList.toArray(new String[] {});
	}

	public String[] getSavedFieldNames()
	{
		return savedFields;
	}

	public HashMap<String, Class> getFieldToTypeMap()
	{
		return fieldToTypeMap;
	}

	public Class getTypeOfField(String fieldName)
	{
		return fieldToTypeMap.get(fieldName);
	}

	public TaggedClass getTaggedClassFromObject(Object objectSaved)
	{
		TaggedClass data = new TaggedClass();
		Class c = data.type = objectSaved.getClass();
		Field f;
		Object obj;

		try
		{
			data.uniqueKey = data.new SavedField();
			if (isUniqueKeyField)
			{
				f = c.getDeclaredField(uniqueKey);
				f.setAccessible(true);
				data.uniqueKey.name = f.getName();
				data.uniqueKey.type = f.getType();
				data.uniqueKey.value = f.get(objectSaved);
			}
			else
			{
				Method m;
				m = c.getDeclaredMethod(uniqueKey, new Class[] {});
				m.setAccessible(true);
				data.uniqueKey.name = m.getName() + "()"; // idk.. what should
															// it be??
				data.uniqueKey.type = m.getReturnType();
				data.uniqueKey.value = m.invoke(objectSaved, new Object[] {});
			}
		}
		catch (Exception e)
		{
			OutputHandler.SOP("Reflection error trying to get UniqueLoadingKey from " + objectSaved.getClass() + ". FE will continue without saving this.");
			e.printStackTrace();
		}

		Class currentClass = c;
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
					if (TypeTagger.isTypeComplex(obj))
					{
						// This object is not a primitive. Call this function on
						// the appropriate TypeTagger.
						obj = DataStorageManager.getTaggerForType(obj.getClass()).getTaggedClassFromObject(obj);
					}
					data.addField(data.new SavedField(savedFields[i], obj));
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
					OutputHandler.SOP("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
					e.printStackTrace();
				}
				--i;
			}
			catch (Throwable e)
			{
				// This... Should not happen. Unless something stupid.
				OutputHandler.SOP("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
				e.printStackTrace();
			}
		}

		return data;
	}

	/**
	 * Reocnstructs the object from its TaggedClass
	 * 
	 * @param data
	 * @return
	 */
	public Object createFromFields(TaggedClass data)
	{
		Object value = null;
		for (TaggedClass.SavedField field : data.TaggedMembers.values())
		{
			if (field.value instanceof TaggedClass)
			{
				field.value = DataStorageManager.getTaggerForType(getTypeOfField(field.name)).createFromFields((TaggedClass) field.value);
			}
		}

		try
		{
			Method reconstructor = forType.getDeclaredMethod(reconstructorMethod, TaggedClass.class);
			reconstructor.setAccessible(true);
			value = reconstructor.invoke(null, data);
		}
		catch (Throwable thrown)
		{
			OutputHandler.felog.log(Level.SEVERE, "Error loading " + data.type + " with name " + data.uniqueKey, thrown);
		}

		return value;
	}

	private Object savedFieldToObject(TaggedClass.SavedField field)
	{
		Object obj = null;
		// If the value of the field is a TaggedClass, run this function on it
		// to recreate the original object.
		if (field.value instanceof TaggedClass)
		{
			obj = DataStorageManager.getTaggerForType(field.type).createFromFields((TaggedClass) field.value);
		}
		else
		{
			// Simple case.
			obj = field.value;
		}
		return obj;
	}

	/**
	 * @param t
	 *            Type of object to check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save the object.
	 */
	public static boolean isTypeComplex(Object obj)
	{
		boolean flag = true;

		if (obj instanceof Integer || obj instanceof int[] || obj instanceof Float || obj instanceof Double || obj instanceof double[]
				|| obj instanceof Boolean || obj instanceof boolean[] || obj instanceof String || obj instanceof String[])
		{
			flag = false;
		}

		return flag;
	}

	/**
	 * @param t
	 *            class check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save this type of object.
	 */
	public static boolean isTypeComplex(Class obj)
	{
		boolean flag = true;
		if (obj.isPrimitive() || obj.equals(Integer.class) || obj.equals(int[].class) || obj.equals(Float.class) || obj.equals(Double.class)
				|| obj.equals(double[].class) || obj.equals(Boolean.class) || obj.equals(boolean[].class) || obj.equals(String.class)
				|| obj.equals(String[].class))
		{
			flag = false;
		}

		return flag;
	}
}
