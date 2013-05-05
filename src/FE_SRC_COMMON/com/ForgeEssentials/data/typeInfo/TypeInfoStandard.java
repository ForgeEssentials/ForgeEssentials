package com.ForgeEssentials.data.typeInfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;

import com.ForgeEssentials.data.StorageManager;
import com.ForgeEssentials.data.api.ClassContainer;
import com.ForgeEssentials.data.api.DataStorageManager;
import com.ForgeEssentials.data.api.IReconstructData;
import com.ForgeEssentials.data.api.ITypeInfo;
import com.ForgeEssentials.data.api.SaveableObject;
import com.ForgeEssentials.data.api.TypeData;
import com.ForgeEssentials.data.api.SaveableObject.Reconstructor;
import com.ForgeEssentials.data.api.SaveableObject.SaveableField;
import com.ForgeEssentials.data.api.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.util.OutputHandler;

/**
 * This is the standard TypeInfo class for all classes that don't have the override.
 * The Build Method will throw an exception if its not correct.
 * @author AbrarSyed
 * @param <T> This would be set to Object, but subclasses may want to have TypeInfo's for very specific classes.
 */
public class TypeInfoStandard implements ITypeInfo<Object>
{
	Class<?>								type;
	private boolean							isUniqueKeyField;
	private boolean							inLine;
	private String							uniqueKey;
	private String							reconstructorMethod;
	private HashMap<String, ClassContainer>	fields;

	public TypeInfoStandard(Class<?> type)
	{
		this.type = type;
		fields = new HashMap<String, ClassContainer>();
	}

	@Override
	public void build()
	{
		SaveableObject AObj = type.getAnnotation(SaveableObject.class);
		inLine = AObj.SaveInline();

		Class<?> currentType = type;
		Class<?> tempType;
		Type aTempType;
		ClassContainer tempContainer;
		SaveableField info;

		HashSet<String> overrides = new HashSet<String>();

		// Iterate through this class and superclass's and get saveable fields
		do
		{
			// Locate all members that are saveable.
			for (Field f : currentType.getDeclaredFields())
			{
				if (overrides.contains(f.getName()))
				{
					overrides.remove(f.getName());
					continue;
				}

				// if its a saveable field
				if (f.isAnnotationPresent(SaveableField.class))
				{
					info = f.getAnnotation(SaveableField.class);

					// register ignoire classes....
					if (info != null && !info.overrideParent().isEmpty())
					{
						overrides.add(info.overrideParent());
					}

					tempType = f.getType();
					aTempType = f.getGenericType();
					if (aTempType instanceof ParameterizedType)
					{
						Type[] types = ((ParameterizedType) aTempType).getActualTypeArguments();
						Class<?>[] params = new Class[types.length];
						for (int i = 0; i < types.length; i++)
						{
							if (types[i] instanceof Class)
							{
								params[i] = (Class<?>) types[i];
							}
							else if (types[i] instanceof ParameterizedType)
							{
								params[i] = (Class<?>) ((ParameterizedType) types[i]).getRawType();
							}
						}

						tempContainer = new ClassContainer(tempType, params);
						fields.put(f.getName(), tempContainer);
					}
					else
					{
						tempContainer = new ClassContainer(tempType);
						fields.put(f.getName(), tempContainer);
					}
				}

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
				{
					new RuntimeException("The reconstructor method must have no paremeters");
				}

				if (!m.getReturnType().isPrimitive() && !m.getReturnType().equals(String.class))
					throw new RuntimeException("The UniqueLoadingKey method must return a primitive or a string");

				uniqueKey = m.getName();
				isUniqueKeyField = false;
			}
		}
	}

	@Override
	public ClassContainer getTypeOfField(String field)
	{
		if (field == null)
			return null;

		return fields.get(field);
	}

	@Override
	public TypeData getTypeDataFromObject(Object objectSaved)
	{
		Class<?> c = objectSaved.getClass();
		TypeData data = DataStorageManager.getDataForType(new ClassContainer(type));
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

		String[] keys = fields.keySet().toArray(new String[fields.size()]);
		Class<?> currentClass = c;
		// Iterate over the object grabbing the fields we want to examine.
		for (int i = 0; i < keys.length; ++i)
		{
			try
			{
				f = currentClass.getDeclaredField(keys[i]);
				f.setAccessible(true);
				obj = f.get(objectSaved);

				if (obj != null)
				{
					if (StorageManager.isTypeComplex(obj.getClass()))
					{
						// This object is not a primitive. Call this function on the appropriate TypeTagger.
						obj = DataStorageManager.getDataForObject(fields.get(keys[i]), obj);
					}
					data.putField(keys[i], obj);
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
	public String[] getFieldList()
	{
		return fields.keySet().toArray(new String[fields.size()]);
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

	@Override
	public boolean canSaveInline()
	{
		return inLine;
	}

	@Override
	public ClassContainer getType()
	{
		return new ClassContainer(type);
	}

	@Override
	public Class<?>[] getGenericTypes()
	{
		return null;
	}

	@Override
	public ITypeInfo<?> getInfoForField(String field)
	{
		return DataStorageManager.getInfoForType(getTypeOfField(field));
	}

}
