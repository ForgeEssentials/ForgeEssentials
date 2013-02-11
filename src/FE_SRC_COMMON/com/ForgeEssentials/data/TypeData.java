package com.ForgeEssentials.data;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.AbstractTypeData;
import com.ForgeEssentials.api.data.AbstractTypeData;
import com.ForgeEssentials.api.data.SavedField;
import com.ForgeEssentials.data.typeData.SpecialSaveableData;
import com.ForgeEssentials.util.OutputHandler;

public class TypeData extends AbstractTypeData
{
	private HashMap<String, SavedField>	members;

	protected TypeData(Class c)
	{
		super(c);
		members = new HashMap<String, SavedField>();
	}

	@Override
	public void addField(SavedField field)
	{
		members.put(field.name, field);
	}

	@Override
	public Object getFieldValue(String name)
	{
		if (members.containsKey(name))
				return members.get(name).value;
		else
			return null;
	}
	
	@Override
	public Set<SavedField> getAllFields()
	{
		Set<SavedField> set = new HashSet<SavedField>();
		set.addAll(members.values());
		return set;
	}
	
	public static AbstractTypeData getTaggedClass(Object obj)
	{
		if (SpecialSaveableData.hasOverrideType(obj.getClass(), true))
		{
			Class override = SpecialSaveableData.getOverrideType(obj.getClass());

			Constructor c;
			try
			{
				c = override.getConstructor(new Class[] { obj.getClass() });
				TypeData tagged = (TypeData) c.newInstance(obj);
				return tagged;
			}
			catch (Exception e)
			{
				OutputHandler.exception(Level.SEVERE, "Error invoking construction of TypeOverride for " + obj.getClass().getCanonicalName(), e);
			}
		}

		return new TypeData(obj.getClass());
	}

	public static AbstractTypeData getTaggedClass(Class c)
	{
		if (SpecialSaveableData.hasOverrideType(c, true))
		{
			Class override = SpecialSaveableData.getOverrideType(c);

			Constructor constructor;
			try
			{
				constructor = override.getConstructor(new Class[] { Class.class });
				TypeData tagged = (TypeData) constructor.newInstance(c);
				return tagged;
			}
			catch (Exception e)
			{
				OutputHandler.exception(Level.SEVERE, "Error invoking construction of TypeOverride for " + c.getCanonicalName(), e);
			}
		}

		return new TypeData(c);
	}
}
