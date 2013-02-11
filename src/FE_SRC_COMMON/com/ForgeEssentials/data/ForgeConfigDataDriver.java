package com.ForgeEssentials.data;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.SavedField;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * 
 * @author AbrarSyed
 * 
 */
public class ForgeConfigDataDriver extends TextDataDriver
{

	@Override
	protected String getExtension()
	{
		return "cfg";
	}

	@Override
	protected boolean saveData(Class type, TypeData objectData)
	{
		boolean wasSuccessful = false;

		File file = getFilePath(type, objectData.uniqueKey.value);

		// Wipe existing Forge Configuration file - they don't take new data.
		if (file.exists())
		{
			file.delete();
		}

		Configuration cfg = new Configuration(file, true);

		for (SavedField field : objectData.TaggedMembers.values())
		{
			writeFieldToProperty(cfg, type.getSimpleName(), field);
		}

		cfg.save();

		return wasSuccessful;
	}

	@Override
	protected TypeData loadData(Class type, Object uniqueKey)
	{
		Configuration cfg = new Configuration(getFilePath(type, uniqueKey), true);
		cfg.load();
		TypeInfo tag = DataStorageManager.getTaggerForType(type);

		TypeData data = readClassFromProperty(cfg, cfg.categories.get(type.getSimpleName()), type);
		data.addField(new SavedField(tag.uniqueKey, uniqueKey));

		return data;
	}

	@Override
	protected TypeData[] loadAll(Class type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<TypeData> data = new ArrayList<TypeData>();

		if (files != null)
		{
			for (File file : files)
			{
				if (!file.isDirectory() && file.getName().endsWith(".cfg"))
				{
					data.add(loadData(type, file.getName().replace(".cfg", "")));
				}
			}
		}

		return data.toArray(new TypeData[] {});
	}

	private void writeFieldToProperty(Configuration cfg, String category, SavedField field)
	{
		if (field == null || field.type == null)
		{
			// ignore.
		}
		else if (field.type.equals(Integer.class))
		{
			cfg.get(category, field.name, ((Integer) field.value).intValue());
		}
		else if (field.type.equals(int[].class))
		{
			cfg.get(category, field.name, (int[]) field.value);
		}
		else if (field.type.equals(Float.class))
		{
			cfg.get(category, field.name, ((Float) field.value).floatValue());
		}
		else if (field.type.equals(Double.class))
		{
			cfg.get(category, field.name, ((Double) field.value).doubleValue());
		}
		else if (field.type.equals(double[].class))
		{
			cfg.get(category, field.name, (double[]) field.value);
		}
		else if (field.type.equals(Boolean.class))
		{
			cfg.get(category, field.name, ((Boolean) field.value).booleanValue());
		}
		else if (field.type.equals(boolean[].class))
		{
			cfg.get(category, field.name, (boolean[]) field.value);
		}
		else if (field.type.equals(String.class))
		{
			cfg.get(category, field.name, (String) field.value);
		}
		else if (field.type.equals(String[].class))
		{
			cfg.get(category, field.name, (String[]) field.value);
		}
		else if (field.type.equals(TypeData.class))
		{
			TypeData tag = (TypeData) field.value;
			String newcat = category + "." + field.name;

			for (SavedField f : tag.TaggedMembers.values())
			{
				writeFieldToProperty(cfg, newcat, f);
			}
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}

	private Object readFieldFromProperty(Configuration cfg, String category, SavedField field)
	{
		if (field.type.equals(int.class))
		{
			return cfg.get(category, field.name, 0).getInt();
		}
		else if (field.type.equals(int[].class))
		{
			return cfg.get(category, field.name, new int[] {}).getIntList();
		}
		else if (field.type.equals(float.class))
		{
			return (float) cfg.get(category, field.name, 0d).getDouble(0);
		}
		else if (field.type.equals(double.class))
		{
			return cfg.get(category, field.name, 0d).getDouble(0);
		}
		else if (field.type.equals(double[].class))
		{
			return cfg.get(category, field.name, new double[] {}).getDoubleList();
		}
		else if (field.type.equals(boolean.class))
		{
			return cfg.get(category, field.name, false).getBoolean(false);
		}
		else if (field.type.equals(boolean[].class))
		{
			return cfg.get(category, field.name, new boolean[] {}).getBooleanList();
		}
		else if (field.type.equals(String.class))
		{
			return cfg.get(category, field.name, "").value;
		}
		else if (field.type.equals(String[].class))
		{
			return cfg.get(category, field.name, new String[] {}).valueList;
		}
		else
		{
			// this should never happen...
			return null;
		}
	}

	private TypeData readClassFromProperty(Configuration cfg, ConfigCategory cat, Class type)
	{
		TypeData data = (TypeData) TypeData.getTaggedClass(type);
		TypeInfo tag = DataStorageManager.getTaggerForType(type);

		if (cat != null)
		{
			SavedField field;

			for (Property prop : cat.getValues().values())
			{
				if (tag.isUniqueKeyField && prop.getName().equals(tag.uniqueKey))
				{
					field = new SavedField();
					field.name = tag.uniqueKey;
					field.type = tag.getTypeOfField(field.name);
					field.value = readFieldFromProperty(cfg, cat.getQualifiedName(), field);
					data.uniqueKey = field;
					continue;
				}

				field = new SavedField();
				field.name = prop.getName();
				field.type = tag.getTypeOfField(field.name);
				field.value = readFieldFromProperty(cfg, cat.getQualifiedName(), field);
				data.addField(field);
			}

			for (ConfigCategory child : cfg.categories.values())
			{
				if (child.isChild() && child.parent == cat) // intentional use
															// of ==
				{
					field = new SavedField();
					field.name = child.getQualifiedName().replace(cat.getQualifiedName() + ".", "");
					field.type = tag.getTypeOfField(field.name);

					if (field.type == null)
					{
						continue;
					}

					field.value = readClassFromProperty(cfg, child, field.type);
					data.addField(field);
				}
			}
		}

		return data;
	}
}
