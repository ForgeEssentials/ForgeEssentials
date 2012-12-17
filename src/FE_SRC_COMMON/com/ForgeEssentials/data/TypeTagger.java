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
	protected String reconstructorMethod;
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
		
		// Iterate through this class and superclass's and get saveable fields
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				if (f.isAnnotationPresent(SaveableField.class))
				{
					SaveableField sf = (SaveableField)f.getAnnotation(SaveableField.class);
					if (sf.uniqueLoadingField())
					{
						// something was previously set a Primary Field
						if (loadingField != null && type.equals(currentType))
							throw new RuntimeException("Only 1 field may have be a unique loading field");
						else if(!f.getType().isPrimitive() && !f.getType().equals(String.class))
							throw new RuntimeException("Unique loading fields must be primitives or strings");
						
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
		
		// find reconstructor method
		for (Method m : type.getDeclaredMethods())
		{
			if (m.getAnnotation(Reconstructor.class) == null)
				continue;
			
			assert Modifier.isStatic(m.getModifiers()) : new RuntimeException("The reconstructor method must be static!");
			assert m.getReturnType().equals(type) : new RuntimeException("The reconstructor method must return "+type);
			assert m.getParameterTypes().length == 1 : new RuntimeException("The reconstructor method must have exactly 1 paremeter/argument");
			assert m.getParameterTypes()[0].equals(TaggedClass.class) : new RuntimeException("The reconstructor method must have a "+TaggedClass.class+" parameter");
			assert reconstructorMethod == null : new RuntimeException("Each class may only have 1 reconstructor method");
			
			reconstructorMethod = m.getName();
		}
		
		this.savedFields = tempList.toArray(new String[] {});
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
		Class c = data.Type = objectSaved.getClass();
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
			catch (Throwable e)
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
		try
		{
			Method reconstructor = data.Type.getMethod(reconstructorMethod, TaggedClass.class);
			reconstructor.setAccessible(true);
			Object obj = reconstructor.invoke(null, data);
		}
		catch (Throwable thrown)
		{
			OutputHandler.felog.log(Level.SEVERE, "Error loading " + data.Type + " with name " + data.LoadingKey.Value, thrown);
		}
		
		return null;
	}
	
	private Object savedFieldToObject(TaggedClass.SavedField field)
	{
		Object obj = null;
		// If the value of the field is a TaggedClass, run this function on it to recreate the original object.
		if (field.Value instanceof TaggedClass)
			obj = this.parent.getTaggerForType(field.Type).createFromFields((TaggedClass)field.Value);
		else
			// Simple case.
			obj = field.Value;
		return obj;
	}
	
	/**
	 * @param t Type of object to check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save the object.
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
	
	/**
	 * @param t class check
	 * @return True if TypeTagger must create a nested TaggedClass to allow DataDrivers to correctly save this type of object.
	 */
	private boolean isTypeComplex(Class obj)
	{
		if ( obj.isPrimitive() || obj.equals(Integer.class) || obj.equals(int[].class) ||
				obj.equals(Float.class) || obj.equals(Double.class) || obj.equals(double[].class) ||
				obj.equals(Boolean.class) || obj.equals(boolean[].class) || obj.equals(String.class) || 
				obj.equals(String[].class))
		{
			return false;
		}
		
		return true;
	}
}
