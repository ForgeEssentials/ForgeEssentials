package com.ForgeEssentials.data;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.logging.Level;

import com.ForgeEssentials.api.data.ITaggedClass;
import com.ForgeEssentials.data.typeOverrides.SpecialSaveableType;
import com.ForgeEssentials.util.OutputHandler;

public class TaggedClass implements ITaggedClass
{
	private Class							type;
	protected SavedField					uniqueKey;
	protected HashMap<String, SavedField>	TaggedMembers;

	protected TaggedClass(Class c)
	{
		type = c;
		TaggedMembers = new HashMap<String, SavedField>();
	}

	protected void addField(SavedField field)
	{
		TaggedMembers.put(field.name, field);
	}

	public Object getFieldValue(String name)
	{
		Object value = null;
		if (uniqueKey != null && (!uniqueKey.name.endsWith("()") && uniqueKey.name.equals(name)))
		{
			value = uniqueKey.value;
		}
		else
		{
			if (TaggedMembers.containsKey(name))
			{
				value = TaggedMembers.get(name).value;
			}
		}

		return value;
	}

	@Override
	public String toString()
	{
		StringBuilder s = new StringBuilder("{");
		s.append("type=").append(getType()).append(", ");
		s.append("unique=").append(uniqueKey).append(", ");

		s.append("[");
		for (Entry<String, SavedField> e : TaggedMembers.entrySet())
		{
			s.append(e.getKey()).append("=").append(e.getValue()).append(", ");
		}
		s.replace(s.length() - 2, s.length(), "]");

		s.append("}");

		return s.toString();
	}

	public Class getType()
	{
		return type;
	}

	public static TaggedClass getTaggedClass(Object obj)
	{
		if (SpecialSaveableType.hasOverrideType(obj.getClass(), true))
		{
			Class override = SpecialSaveableType.getOverrideType(obj.getClass());

			Constructor c;
			try
			{
				c = override.getConstructor(new Class[] { obj.getClass() });
				TaggedClass tagged = (TaggedClass) c.newInstance(obj);
				return tagged;
			}
			catch (Exception e)
			{
				OutputHandler.exception(Level.SEVERE, "Error invoking construction of TypeOverride for "+obj.getClass().getCanonicalName(), e);
			}
		}

		return new TaggedClass(obj.getClass());
	}

	public static TaggedClass getTaggedClass(Class c)
	{
		if (SpecialSaveableType.hasOverrideType(c, true))
		{
			Class override = SpecialSaveableType.getOverrideType(c);

			Constructor constructor;
			try
			{
				constructor = override.getConstructor(new Class[] { Class.class});
				TaggedClass tagged = (TaggedClass) constructor.newInstance(c);
				return tagged;
			}
			catch (Exception e)
			{
				OutputHandler.exception(Level.SEVERE, "Error invoking construction of TypeOverride for "+c.getCanonicalName(), e);
			}
		}

		return new TaggedClass(c);
	}
}
