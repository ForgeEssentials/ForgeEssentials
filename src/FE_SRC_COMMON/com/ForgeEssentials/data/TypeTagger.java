package com.ForgeEssentials.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.util.OutputHandler;

/**
 * Responsible for transforming a particular type (class) into a TaggedClass for easier
 * storage via DataDrivers, and eventually converts a TaggedClass into the original Object
 * (More or less.) 
 * 
 * @author MysteriousAges
 *
 */
public class TypeTagger
{
	private DataDriver parent;
	protected Class forType;
	protected String loadingField;
	protected String[] savedFields;
	protected HashMap<String, Class> fieldToTypeMap;

	public TypeTagger(DataDriver driver, Class type)
	{
		this.parent = driver;
		this.forType = type;
		Class currentType = this.forType;
		this.fieldToTypeMap = new HashMap<String, Class>();

		ArrayList<String> tempList = new ArrayList<String>();
		Annotation a;
		
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				if ((a = f.getAnnotation(SaveableField.class)) != null)
				{
					SaveableField sf = (SaveableField)a;
					if (sf.uniqueLoadingField())
					{
						loadingField = f.getName();
					}
					else
					{
						tempList.add(f.getName());
					}

					fieldToTypeMap.put(f.getName(), f.getType());
				}
			}
		}
		while ((currentType = currentType.getSuperclass()) != null);
		
		this.savedFields = tempList.toArray(new String[tempList.size()]);
	}
	
	protected DataDriver getParent()
	{
		return this.parent;
	}
	
	public Class getTypeOfField(String fieldName)
	{
		return this.fieldToTypeMap.get(fieldName);
	}
	
	public TaggedClass getTaggedClassFromObject(Object objectSaved)
	{
		TaggedClass data = new TaggedClass();
		data.Type = objectSaved.getClass();
		Class c = objectSaved.getClass();
		Field f;
		Object obj;		
		
		// And the loading field.
		if (loadingField != null)
		{
			try
			{
				f = c.getDeclaredField(loadingField);
				f.setAccessible(true);
				data.LoadingKey = data.new SavedField(loadingField, f.get(objectSaved));
				
			}
			catch (Exception e)
			{
				OutputHandler.SOP("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
				e.printStackTrace();
			}
		}
		
		Class currentClass = c;
		// Iterate over the object grabbing the fields we want to examine.
		for (int i = 0; i < this.savedFields.length; ++i)
		{
			try
			{
				f = currentClass.getDeclaredField(savedFields[i]);
				f.setAccessible(true);
				obj = f.get(objectSaved);
				if (obj != null)
				{
					if (this.isTypeComplex(obj))
					{
						// This object is not a primitive. Call this function on the appropriate TypeTagger.
						obj = this.parent.getTaggerForType(obj.getClass()).getTaggedClassFromObject(obj);
					}
					data.addField(data.new SavedField(savedFields[i], obj));
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
					OutputHandler.SOP("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
					e.printStackTrace();
				}
				--i;
			}
			catch (Exception e)
			{
				// This... Should not happen. Unless something stupid.
				OutputHandler.SOP("Reflection error trying to save " + objectSaved.getClass() + ". FE will continue without saving this.");
				e.printStackTrace();
			}
		}
		
		return data;
	}
	
	public Object createFromFields(TaggedClass data)
	{
		Object newObject = null;
		Object obj;
		TaggedClass.SavedField fieldInfo;
		Field f;
		
		try
		{
			// Attempt to create the object. 
			newObject = this.forType.newInstance();
			
			// Loop through each field and populate.
			for (int i = 0; i < data.TaggedMembers.size(); ++i)
			{
				try
				{
					obj = this.savedFieldToObject(data.TaggedMembers.get(i));

					// Set the value of the field.
					f = this.forType.getDeclaredField(data.TaggedMembers.get(i).FieldName);
					f.setAccessible(true);
					f.set(newObject, obj);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			// Don't forget about the loading field.
			if (data.LoadingKey != null)
			{
				try
				{
					obj = this.savedFieldToObject(data.LoadingKey);
					
					f = this.forType.getDeclaredField(data.LoadingKey.FieldName);
					f.setAccessible(true);
					f.set(newObject, obj);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			// If we can't create the object using its default constructor, just give up.
			e.printStackTrace();
		}
		
		return newObject;
	}
	
	private Object savedFieldToObject(TaggedClass.SavedField field)
	{
		Object obj = null;
		// If the value of the field is a TaggedClass, run this function on it to recreate the original object.
		if (field.Value instanceof TaggedClass)
		{
			obj = this.parent.getTaggerForType(field.Type).createFromFields((TaggedClass)field.Value);
		}
		else
		{
			// Simple case.
			obj = field.Value;
		}
		return obj;
	}
	
	/**
	 * 
	 * @param t Type of object to check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to corretly save the object.
	 */
	private boolean isTypeComplex(Object obj)
	{
		boolean flag = true;
		
		if (obj instanceof Integer || obj instanceof int[] ||
				obj instanceof Float || obj instanceof Double || obj instanceof double[] ||
				obj instanceof Boolean || obj instanceof boolean[] || obj instanceof String || 
				obj instanceof String[])
		{
			flag = false;
		}
		
		return flag;
	}
}
