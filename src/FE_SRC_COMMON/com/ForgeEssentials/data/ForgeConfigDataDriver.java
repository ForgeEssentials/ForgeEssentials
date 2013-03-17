package com.ForgeEssentials.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * @author AbrarSyed
 */
@SuppressWarnings(value = { "rawtypes" })
public class ForgeConfigDataDriver extends TextDataDriver
{

	@Override
	protected String getExtension()
	{
		return "cfg";
	}

	@Override
	protected boolean saveData(ClassContainer type, TypeData objectData)
	{
		boolean wasSuccessful = false;

		File file = getFilePath(type, objectData.getUniqueKey());

		// Wipe existing Forge Configuration file - they don't take new data.
		if (file.exists())
		{
			file.delete();
		}

		Configuration cfg = new Configuration(file, true);

		// write each and every field to the config file.
		for (Entry<String, Object> entry : objectData.getAllFields())
		{
			writeFieldToProperty(cfg, type.getFileSafeName(), entry.getKey(), entry.getValue());
		}

		cfg.save();

		return wasSuccessful;
	}

	@Override
	protected TypeData loadData(ClassContainer type, String uniqueKey)
	{
		File file = getFilePath(type, uniqueKey);

		if (!file.exists())
			return null;

		Configuration cfg = new Configuration(file, true);
		cfg.load();
		ITypeInfo info = DataStorageManager.getInfoForType(type);
		TypeData data = DataStorageManager.getDataForType(type);
		readClassFromProperty(cfg, cfg.categories.get(type.getFileSafeName()), data, info);
		data.setUniqueKey(uniqueKey);

		return data;
	}

	@Override
	protected TypeData[] loadAll(ClassContainer type)
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

	private void writeFieldToProperty(Configuration cfg, String category, String name, Object obj)
	{
		if (name == null || obj == null)
			// ignore...
			return;

		Class type = obj.getClass();

		if (type.equals(Integer.class))
		{
			cfg.get(category, name, ((Integer) obj).intValue());
		}
		else if (type.equals(int[].class))
		{
			cfg.get(category, name, (int[]) obj);
		}
		else if (type.equals(Byte.class))
		{
			cfg.get(category, name, ((Byte) obj).intValue());
		}
		else if (type.equals(byte[].class))
		{
			int[] array = new int[((byte[]) obj).length];

			for (int i = 0; i < ((byte[]) obj).length; i++)
			{
				array[i] = ((byte[]) obj)[i];
			}

			cfg.get(category, name, array);
		}
		else if (type.equals(Float.class))
		{
			cfg.get(category, name, ((Float) obj).floatValue());
		}
		else if (type.equals(float[].class))
		{
			double[] array = new double[((float[]) obj).length];

			for (int i = 0; i < ((float[]) obj).length; i++)
			{
				array[i] = ((float[]) obj)[i];
			}

			cfg.get(category, name, array);
		}
		else if (type.equals(Double.class))
		{
			cfg.get(category, name, ((Double) obj).doubleValue());
		}
		else if (type.equals(double[].class))
		{
			cfg.get(category, name, (double[]) obj);
		}
		else if (type.equals(Long.class))
		{
			cfg.get(category, name, ((Long) obj).intValue());
		}
		else if (type.equals(long[].class))
		{
			int[] array = new int[((long[]) obj).length];

			for (int i = 0; i < ((long[]) obj).length; i++)
			{
				array[i] = (int) ((long[]) obj)[i];
			}

			cfg.get(category, name, array);
		}
		else if (type.equals(Boolean.class))
		{
			cfg.get(category, name, ((Boolean) obj).booleanValue());
		}
		else if (type.equals(boolean[].class))
		{
			cfg.get(category, name, (boolean[]) obj);
		}
		else if (type.equals(String.class))
		{
			cfg.get(category, name, (String) obj);
		}
		else if (type.equals(String[].class))
		{
			cfg.get(category, name, (String[]) obj);
		}
		else if (type.equals(TypeData.class))
		{
			TypeData data = (TypeData) obj;
			String newcat = category + "." + name;

			for (Entry<String, Object> entry : data.getAllFields())
			{
				writeFieldToProperty(cfg, newcat, entry.getKey(), entry.getValue());
			}
		}
		else
			throw new IllegalArgumentException("Cannot save object type. " + obj.getClass() + "  instance: " + obj);
	}

	private Object readFieldFromProperty(Configuration cfg, String category, String name, Class type)
	{
		if (type.equals(int.class))
			return cfg.get(category, name, 0).getInt();
		if (type.equals(byte.class))
			return (byte) cfg.get(category, name, 0).getInt();
		else if (type.equals(int[].class))
			return cfg.get(category, name, new int[] {}).getIntList();
		else if (type.equals(byte[].class))
		{
			int[] array = cfg.get(category, name, new int[] {}).getIntList();
			byte[] bArray = new byte[array.length];

			for (int i = 0; i < array.length; i++)
			{
				bArray[i] = (byte) array[i];
			}
			return bArray;
		}
		else if (type.equals(float.class))
			return (float) cfg.get(category, name, 0d).getDouble(0);
		else if (type.equals(float[].class))
		{
			double[] array = cfg.get(category, name, new double[] {}).getDoubleList();
			float[] fArray = new float[array.length];

			for (int i = 0; i < array.length; i++)
			{
				fArray[i] = (float) array[i];
			}
			return fArray;
		}
		else if (type.equals(double.class))
			return cfg.get(category, name, 0d).getDouble(0);
		else if (type.equals(double[].class))
			return cfg.get(category, name, new double[] {}).getDoubleList();
		if (type.equals(long.class))
			return (long) cfg.get(category, name, 0).getInt();
		else if (type.equals(long[].class))
		{
			int[] array = cfg.get(category, name, new int[] {}).getIntList();
			long[] lArray = new long[array.length];

			for (int i = 0; i < array.length; i++)
			{
				lArray[i] = array[i];
			}
			return lArray;
		}
		else if (type.equals(boolean.class))
			return cfg.get(category, name, false).getBoolean(false);
		else if (type.equals(boolean[].class))
			return cfg.get(category, name, new boolean[] {}).getBooleanList();
		else if (type.equals(String.class))
			return cfg.get(category, name, "").value;
		else if (type.equals(String[].class))
			return cfg.get(category, name, new String[] {}).valueList;
		else
			// this should never happen...
			return null;
	}

	private void readClassFromProperty(Configuration cfg, ConfigCategory cat, TypeData data, ITypeInfo info)
	{

		if (cat != null)
		{
			String name;
			ClassContainer newType;
			ITypeInfo newInfo;
			TypeData newData;
			Object value;
			for (Property prop : cat.getValues().values())
			{
				name = prop.getName();
				newType = info.getTypeOfField(name);
				value = readFieldFromProperty(cfg, cat.getQualifiedName(), name, newType.getType());
				data.putField(name, value);
			}

			for (ConfigCategory child : cfg.categories.values())
			{
				if (child.isChild() && child.parent == cat) // intentional use
															// of ==
				{
					name = child.getQualifiedName().replace(cat.getQualifiedName() + ".", "");
					newInfo = info.getInfoForField(name);
					newData = DataStorageManager.getDataForType(newInfo.getType());

					if (newData == null || newInfo == null)
					{
						continue;
					}
					readClassFromProperty(cfg, child, newData, newInfo);
					value = newData;
					data.putField(name, value);
				}
			}
		}
	}
}
