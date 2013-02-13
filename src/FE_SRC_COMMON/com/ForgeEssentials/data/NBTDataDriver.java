package com.ForgeEssentials.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.zip.GZIPOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.api.data.TypeInfoHandler;
import com.ForgeEssentials.util.OutputHandler;

public class NBTDataDriver extends BinaryDataDriver
{
	private static final String UNIQUE = "__UNIQUE__";

	@Override
	protected boolean saveData(Class type, TypeData fieldList)
	{
		boolean successful = true;

		// Create file object
		File file = new File(baseFile + fieldList.getUniqueKey() + ".dat");

		NBTTagCompound compound = new NBTTagCompound();
		writeClassToTag(compound, fieldList);
		writeNBT(compound, file);

		return successful;
	}

	private static void writeNBT(NBTTagCompound tag, File file)
	{
		try
		{
			// create file/folders if they don;t exist..
			if (!file.getParentFile().exists())
			{
				file.getParentFile().mkdirs();
			}

			// make temp file
			File temp = new File(file.getPath() + "_tmp");
			if (temp.exists())
			{
				temp.delete();
			}

			// write temp file
			DataOutputStream stream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(temp)));
			try
			{
				NBTBase.writeNamedTag(tag, stream);
			}
			finally
			{
				stream.close();
			}

			// change from temp to real
			if (file.exists())
			{
				file.delete();
			}

			if (file.exists())
			{
				throw new IOException("Failed to delete " + file);
			}
			else
			{
				temp.renameTo(file);
			}
		}
		catch (Exception e)
		{
			OutputHandler.info("Writing NBT to " + file + " failed");
		}
	}

	private static NBTTagCompound readNBT(File file)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools.readCompressed(new FileInputStream(file));
			return nbt;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected TypeData loadData(Class type, String uniqueKey)
	{
		NBTTagCompound nbt = readNBT(getFilePath(type, uniqueKey));

		if (nbt == null)
		{
			return null;
		}

		return readClassFromTag(nbt, type);
	}

	private void writeClassToTag(NBTTagCompound tag, TypeData fieldList)
	{
		writeFieldToTag(tag, UNIQUE, fieldList.getUniqueKey());
		for (Entry<String, Object> entry : fieldList.getAllFields())
		{
			writeFieldToTag(tag, entry.getKey(), entry.getValue());
		}
	}

	private TypeData readClassFromTag(NBTTagCompound tag, Class type)
	{
		TypeData tClass = (TypeData) DataStorageManager.getDataForType(type);
		ITypeInfo tagger = DataStorageManager.getInfoForType(type);
		
		String unique = readFieldFromTag(tag, unique, tagger);

		return tClass;
	}

	private void writeFieldToTag(NBTTagCompound tag, String name, Object obj)
	{
		if (name == null || obj == null)
		{
			// ignore.
			return;
		}
		
		Class type = obj.getClass();
		
		if (type.equals(Integer.class))
		{
			tag.setInteger(name, (Integer) obj);
		}
		else if (type.equals(int[].class))
		{
			tag.setIntArray(name, (int[]) obj);
		}
		else if (type.equals(Float.class))
		{
			tag.setFloat(name, (Float) obj);
		}
		else if (type.equals(Double.class))
		{
			tag.setDouble(name, (Double) obj);
		}
		else if (type.equals(double[].class))
		{
			NBTTagList list = new NBTTagList();
			double[] array = (double[]) obj;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagDouble(name + "_" + i, array[i]));
			}

			tag.setTag(name, list);
		}
		else if (type.equals(Boolean.class))
		{
			tag.setBoolean(name, (Boolean) obj);
		}
		else if (type.equals(boolean[].class))
		{
			NBTTagList list = new NBTTagList();
			boolean[] array = (boolean[]) obj;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagByte(name + "_" + i, (byte) (array[i] ? 1 : 0)));
			}

			tag.setTag(name, list);
		}
		else if (type.equals(String.class))
		{
			tag.setString(name, (String) obj);
		}
		else if (type.equals(String[].class))
		{
			NBTTagList list = new NBTTagList();
			String[] array = (String[]) obj;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagString(name + "_" + i, array[i]));
			}

			tag.setTag(name, list);
		}
		else if (type.equals(IReconstructData.class))
		{
			NBTTagCompound compound = new NBTTagCompound();
			writeClassToTag(compound, (TypeData) obj);
			tag.setCompoundTag(name, compound);
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}

	private Object readFieldFromTag(NBTTagCompound tag, String name, TypeInfoHandler tagger)
	{
		if (name == null || tagger == null)
		{
			return null;
		}
		
		Class type = tagger.getTypeOfField(name);
		
		if (field.type.equals(int.class))
		{
			return tag.getInteger(field.name);
		}
		else if (field.type.equals(int[].class))
		{
			return tag.getIntArray(field.name);
		}
		else if (field.type.equals(float.class))
		{
			return tag.getFloat(field.name);
		}
		else if (field.type.equals(double.class))
		{
			return tag.getDouble(field.name);
		}
		else if (field.type.equals(double[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			double[] array = new double[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagDouble) list.tagAt(i)).data;
			}

			return array;
		}
		else if (field.type.equals(boolean.class))
		{
			return tag.getBoolean(field.name);
		}
		else if (field.type.equals(boolean[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			boolean[] array = new boolean[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagByte) list.tagAt(i)).data != 0;
			}

			return array;
		}
		else if (field.type.equals(String.class))
		{
			return tag.getString(field.name);
		}
		else if (field.type.equals(String[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			String[] array = new String[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagString) list.tagAt(i)).data;
			}

			return array;
		}
		else if (field.type.equals(IReconstructData.class))
		{
			NBTTagCompound compound = new NBTTagCompound();
			return readClassFromTag(compound, tagger.getTypeOfField(field.name));
		}
		else
		{
			// this should never happen...
			return null;
		}
	}

	@Override
	protected TypeData[] loadAll(Class type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<IReconstructData> data = new ArrayList<IReconstructData>();

		for (File file : files)
		{
			if (!file.isDirectory() && file.getName().endsWith(".dat"))
			{
				data.add(loadData(type, file.getName().replace(".dat", "")));
			}
		}

		return data.toArray(new TypeData[] {});
	}
}
