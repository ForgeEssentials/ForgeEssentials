package com.ForgeEssentials.data.typeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.data.StorageManager;
import com.ForgeEssentials.util.OutputHandler;

public class TypeInfoSerialize<T> implements ITypeInfo<T>
{
	private final ClassContainer container;
	private HashMap<String, Class>	fields;
	String uniqueKey;
	boolean hasUniqueKey = false;
	boolean isUniqueKeyField;

	public TypeInfoSerialize(ClassContainer container)
	{
		this.container = container;
		fields = new HashMap<String, Class>();
		
	}

	@Override
	public boolean canSaveInline()
	{
		return true;
	}

	@Override
	public void build()
	{
		Class currentType = container.type;
		
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				// if its a saveable field
				if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
					fields.put(f.getName(), f.getType());

				// check for UniqueKey
				if (f.isAnnotationPresent(UniqueLoadingKey.class))
				{
					if (uniqueKey != null)
						throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");
					if (!f.getType().isPrimitive() && !f.getType().equals(String.class))
						throw new RuntimeException("The UniqueLoadingKey must be a primitive or a string");

					isUniqueKeyField = true;
					uniqueKey = f.getName();
					hasUniqueKey = true;
				}

			}
		} while ((currentType = currentType.getSuperclass()) != null);
		
		// find reconstructor method
		for (Method m : container.type.getDeclaredMethods())
		{
			// catches the UniqueLoadingKey method variant
			if (m.isAnnotationPresent(UniqueLoadingKey.class))
			{
				if (uniqueKey != null)
					throw new RuntimeException("Each class may only have 1 UniqueLoadingKey");

				if (m.getParameterTypes().length > 0)
					new RuntimeException("The reconstructor method must have no paremeters");

				if (!m.getReturnType().isPrimitive() && !m.getReturnType().equals(String.class))
					throw new RuntimeException("The UniqueLoadingKey method must return a primitive or a string");

				uniqueKey = m.getName();
				isUniqueKeyField = false;
				hasUniqueKey = true;
			}
		}
	}

	@Override
	public Class getTypeOfField(String field)
	{
		if (field == null)
			return null;

		return fields.get(field);
	}

	@Override
	public String[] getFieldList()
	{
		return fields.keySet().toArray(new String[fields.size()]);
	}

	@Override
	public TypeData getTypeDataFromObject(T obj)
	{
		Class c = obj.getClass();
		TypeData data = DataStorageManager.getDataForType(c);
		Field f;
		Object temp;

		// do Unique key stuff
		try
		{
			if (isUniqueKeyField && hasUniqueKey)
			{
				f = c.getDeclaredField(uniqueKey);
				f.setAccessible(true);
				data.setUniqueKey(f.get(obj).toString());
			}
			else if (hasUniqueKey)
			{
				Method m;
				m = c.getDeclaredMethod(uniqueKey, new Class[] {});
				m.setAccessible(true);
				Object val = m.invoke(obj, new Object[] {});
				data.setUniqueKey(val.toString());

			}
			else
			{
				data.setUniqueKey(obj.toString());
			}
		}
		catch (Exception e)
		{
			OutputHandler.exception(Level.SEVERE, "Reflection error trying to get UniqueLoadingKey from " + obj.getClass() + ". FE will continue without saving this.", e);
		}

		String[] keys = fields.keySet().toArray(new String[fields.size()]);
		Class currentClass = c;
		// Iterate over the object grabbing the fields we want to examine.
		for (int i = 0; i < keys.length; ++i)
		{
			try
			{
				f = currentClass.getDeclaredField(keys[i]);
				f.setAccessible(true);
				temp = f.get(obj);

				if (temp != null)
				{
					if (StorageManager.isTypeComplex(temp.getClass()))
					{
						// This object is not a primitive. Call this function on the appropriate TypeTagger.
						temp = DataStorageManager.getInfoForType(temp.getClass()).getTypeDataFromObject(temp);
					}
					data.putField(keys[i], temp);
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
					OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + obj.getClass() + ". FE will continue without saving this.", e);
				}
				--i;
			}
			catch (Exception e)
			{
				// This... Should not happen. Unless something stupid.
				OutputHandler.exception(Level.SEVERE, "Reflection error trying to save " + obj.getClass() + ". FE will continue without saving this.", e);
			}
		}

		return data;
	}

	@Override
	public T reconstruct(IReconstructData data)
	{
		try
		{
			Object obj = container.type.newInstance();
			Class currentType = data.getType();
			
			do
			{
				// Locate all members that are saveable.
				for (Field f : currentType.getDeclaredFields())
				{
					// if its a saveable field
					if (!Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
					{
						f.set(obj, data.getFieldValue(f.getName()));
					}

				}
			} while ((currentType = currentType.getSuperclass()) != null);
			
		}
		catch (Throwable thrown)
		{
			OutputHandler.exception(Level.SEVERE, "Error loading " + data.getType() + " with name " + data.getUniqueKey(), thrown);
		}
		
		return null;
	}

	@Override
	public Class<? extends T> getType()
	{
		return container.type;
	}

	@Override
	public Class[] getGenericTypes()
	{
		return container.getParameters();
	}

	@Override
	public ITypeInfo getInfoForField(String field)
	{
		return DataStorageManager.getInfoForType(getTypeOfField(field));
	}

}
