package com.ForgeEssentials.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagString;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.ITypeInfo;
import com.ForgeEssentials.api.data.TypeData;
import com.ForgeEssentials.util.OutputHandler;

public class NBTDataDriver extends BinaryDataDriver
{
	@Override
	protected boolean saveData(ClassContainer type, TypeData data)
	{
		boolean successful = true;

		// Create file object
		File file = getFilePath(type, data.getUniqueKey());

		NBTTagCompound compound = new NBTTagCompound();
		writeClassToTag(compound, data);
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

			// delete old file
			if (file.exists())
			{
				file.delete();
			}

			if (file.exists())
				throw new IOException("Failed to delete " + file);
			else
			{
				// change from temp to real
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
			OutputHandler.exception(Level.FINEST, "Error tryong to read NBT frole from " + file, e);
			return null;
		}
	}

	@Override
	protected TypeData loadData(ClassContainer type, String uniqueKey)
	{
		NBTTagCompound nbt = readNBT(getFilePath(type, uniqueKey));

		if (nbt == null)
			return null;

		TypeData data = DataStorageManager.getDataForType(type);
		data.setUniqueKey(uniqueKey);
		ITypeInfo info = DataStorageManager.getInfoForType(type);
		readClassFromTag(nbt, data, info);

		return data;
	}

	private void writeClassToTag(NBTTagCompound tag, TypeData data)
	{
		for (Entry<String, Object> entry : data.getAllFields())
		{
			writeFieldToTag(tag, entry.getKey(), entry.getValue());
		}
	}

	private void readClassFromTag(NBTTagCompound tag, TypeData data, ITypeInfo info)
	{
		String name;
		ClassContainer tempType;
		Object val;
		for (NBTBase child : (Collection<NBTBase>) tag.getTags())
		{
			name = child.getName();
			tempType = info.getTypeOfField(name);

			if (StorageManager.isTypeComplex(tempType))
			{
				NBTTagCompound compound = tag.getCompoundTag(name);
				TypeData tempData = DataStorageManager.getDataForType(tempType);
				readClassFromTag(compound, tempData, info.getInfoForField(name));
				val = tempData;
			}
			else
			{
				val = readPrimitiveFromTag(tag, name, tempType.getType());
			}

			data.putField(name, val);
		}
	}

	private void writeFieldToTag(NBTTagCompound tag, String name, Object obj)
	{
		if (name == null || obj == null)
			// ignore.
			return;

		Class<?> type = obj.getClass();

		if (type.equals(Integer.class))
		{
			tag.setInteger(name, (Integer) obj);
		}
		else if (type.equals(int[].class))
		{
			tag.setIntArray(name, (int[]) obj);
		}
		else if (type.equals(Byte.class))
		{
			tag.setByte(name, (Byte) obj);
		}
		else if (type.equals(byte[].class))
		{
			tag.setByteArray(name, (byte[]) obj);
		}
		else if (type.equals(Float.class))
		{
			tag.setFloat(name, (Float) obj);
		}
		else if (type.equals(float[].class))
		{
			NBTTagList list = new NBTTagList();
			float[] array = (float[]) obj;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagFloat(name + "_" + i, array[i]));
			}

			tag.setTag(name, list);
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
		else if (type.equals(Long.class))
		{
			tag.setLong(name, (Long) obj);
		}
		else if (type.equals(long[].class))
		{
			NBTTagList list = new NBTTagList();
			long[] array = (long[]) obj;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagLong(name + "_" + i, array[i]));
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
		else if (IReconstructData.class.isAssignableFrom(type))
		{
			NBTTagCompound compound = new NBTTagCompound();
			writeClassToTag(compound, (TypeData) obj);
			tag.setCompoundTag(name, compound);
		}
		else
			throw new IllegalArgumentException("Cannot save object type: " + type.getCanonicalName());
	}

	private Object readPrimitiveFromTag(NBTTagCompound tag, String name, Class type)
	{
		if (name == null || type == null)
			return null;

		if (type.equals(int.class))
			return tag.getInteger(name);
		else if (type.equals(int[].class))
			return tag.getIntArray(name);
		else if (type.equals(byte.class))
			return tag.getByte(name);
		else if (type.equals(byte[].class))
			return tag.getByteArray(name);
		else if (type.equals(float.class))
			return tag.getFloat(name);
		else if (type.equals(float[].class))
		{
			NBTTagList list = tag.getTagList(name);
			float[] array = new float[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagFloat) list.tagAt(i)).data;
			}

			return array;
		}
		else if (type.equals(double.class))
			return tag.getDouble(name);
		else if (type.equals(double[].class))
		{
			NBTTagList list = tag.getTagList(name);
			double[] array = new double[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagDouble) list.tagAt(i)).data;
			}

			return array;
		}
		if (type.equals(long.class))
			return tag.getInteger(name);
		else if (type.equals(long[].class))
		{
			NBTTagList list = tag.getTagList(name);
			long[] array = new long[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagLong) list.tagAt(i)).data;
			}

			return array;
		}
		else if (type.equals(boolean.class))
			return tag.getBoolean(name);
		else if (type.equals(boolean[].class))
		{
			NBTTagList list = tag.getTagList(name);
			boolean[] array = new boolean[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagByte) list.tagAt(i)).data != 0;
			}

			return array;
		}
		else if (type.equals(String.class))
			return tag.getString(name);
		else if (type.equals(String[].class))
		{
			NBTTagList list = tag.getTagList(name);
			String[] array = new String[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagString) list.tagAt(i)).data;
			}

			return array;
		}

		throw new IllegalArgumentException("NON PRIMITIVE TYPE! " + type);
	}

	@Override
	protected TypeData[] loadAll(ClassContainer type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<IReconstructData> data = new ArrayList<IReconstructData>();

		if (files == null)
			return new TypeData[0];

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
