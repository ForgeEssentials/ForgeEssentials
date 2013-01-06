package com.ForgeEssentials.data;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.GZIPOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.TaggedClass.SavedField;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class NBTDataDriver extends DataDriver
{
	private File baseFile;

	@Override
	public void parseConfigs(Configuration config, String worldName)
	{
		Property prop;

		prop = config.get("Data.NBT", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";

		boolean useFEDir = prop.getBoolean(false);

		if (useFEDir)
		{
			baseFile = new File(ForgeEssentials.FEDIR, "saves/NBT/" + worldName
					+ "/");
		} else
		{
			File parent = FunctionHelper.getBaseDir();
			if (FMLCommonHandler.instance().getSide().isClient())
			{
				parent = new File(FunctionHelper.getBaseDir(), "saves/");
			}

			baseFile = new File(parent, worldName + "/FEData/NBT/");
		}

		config.save();
	}

	private File getTypePath(Class type)
	{
		return new File(baseFile, type.getSimpleName() + "/");
	}

	private File getFilePath(Class type, Object uniqueKey)
	{
		return new File(getTypePath(type), uniqueKey.toString() + ".dat");
	}

	@Override
	protected boolean saveData(Class type, TaggedClass fieldList)
	{
		boolean successful = true;

		// Create file object
		File file = new File(baseFile + fieldList.uniqueKey.toString() + ".dat");

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
			DataOutputStream stream = new DataOutputStream(
					new GZIPOutputStream(new FileOutputStream(temp)));
			try
			{
				NBTBase.writeNamedTag(tag, stream);
			} finally
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
			} else
			{
				temp.renameTo(file);
			}
		} catch (Exception e)
		{
			OutputHandler.SOP("Writing NBT to " + file + " failed");
		}
	}

	private static NBTTagCompound readNBT(File file)
	{
		try
		{
			NBTTagCompound nbt = CompressedStreamTools
					.readCompressed(new FileInputStream(file));
			return nbt;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		NBTTagCompound nbt = readNBT(getFilePath(type, uniqueKey));

		if (nbt == null)
		{
			return null;
		}

		TypeTagger tag = DataStorageManager.getTaggerForType(type);

		return readClassFromTag(nbt, tag);
	}

	private void writeClassToTag(NBTTagCompound tag, TaggedClass tClass)
	{
		for (SavedField field : tClass.TaggedMembers.values())
		{
			writeFieldToTag(tag, field);
		}
	}

	private TaggedClass readClassFromTag(NBTTagCompound tag, TypeTagger tagger)
	{
		TaggedClass tClass = new TaggedClass();

		// not gonna load it if its the method...
		if (tagger.isUniqueKeyField)
		{
			SavedField unique = tClass.new SavedField();
			unique.name = tagger.uniqueKey;
			unique.type = tagger.getTypeOfField(unique.name);
			unique.value = readFieldFromTag(tag, unique, tagger);
			tClass.uniqueKey = unique;
		}

		for (String name : tagger.savedFields)
		{
			SavedField field = tClass.new SavedField();
			field.name = name;
			field.type = tagger.getTypeOfField(name);
			field.value = readFieldFromTag(tag, field, tagger);
			tClass.addField(field);
		}

		return tClass;
	}

	private void writeFieldToTag(NBTTagCompound tag, SavedField field)
	{
		if (field == null || field.type == null || field.value == null)
		{
			// ignore.
		} else if (field.type.equals(Integer.class))
		{
			tag.setInteger(field.name, (Integer) field.value);
		} else if (field.type.equals(int[].class))
		{
			tag.setIntArray(field.name, (int[]) field.value);
		} else if (field.type.equals(Float.class))
		{
			tag.setFloat(field.name, (Float) field.value);
		} else if (field.type.equals(Double.class))
		{
			tag.setDouble(field.name, (Double) field.value);
		} else if (field.type.equals(double[].class))
		{
			NBTTagList list = new NBTTagList();
			double[] array = (double[]) field.value;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagDouble(field.name + "_" + i, array[i]));
			}

			tag.setTag(field.name, list);
		} else if (field.type.equals(Boolean.class))
		{
			tag.setBoolean(field.name, (Boolean) field.value);
		} else if (field.type.equals(boolean[].class))
		{
			NBTTagList list = new NBTTagList();
			boolean[] array = (boolean[]) field.value;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagByte(field.name + "_" + i,
						(byte) (array[i] ? 1 : 0)));
			}

			tag.setTag(field.name, list);
		} else if (field.type.equals(String.class))
		{
			tag.setString(field.name, (String) field.value);
		} else if (field.type.equals(String[].class))
		{
			NBTTagList list = new NBTTagList();
			String[] array = (String[]) field.value;
			for (int i = 0; i < array.length; i++)
			{
				list.appendTag(new NBTTagString(field.name + "_" + i, array[i]));
			}

			tag.setTag(field.name, list);
		} else if (field.type.equals(TaggedClass.class))
		{
			NBTTagCompound compound = new NBTTagCompound();
			writeClassToTag(compound, (TaggedClass) field.value);
			tag.setCompoundTag(field.name, compound);
		} else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}

	private Object readFieldFromTag(NBTTagCompound tag, SavedField field,
			TypeTagger tagger)
	{
		if (field == null || field.type == null || field.value == null)
		{
			return null;
		} else if (field.type.equals(int.class))
		{
			return tag.getInteger(field.name);
		} else if (field.type.equals(int[].class))
		{
			return tag.getIntArray(field.name);
		} else if (field.type.equals(float.class))
		{
			return tag.getFloat(field.name);
		} else if (field.type.equals(double.class))
		{
			return tag.getDouble(field.name);
		} else if (field.type.equals(double[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			double[] array = new double[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagDouble) list.tagAt(i)).data;
			}

			return array;
		} else if (field.type.equals(boolean.class))
		{
			return tag.getBoolean(field.name);
		} else if (field.type.equals(boolean[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			boolean[] array = new boolean[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagByte) list.tagAt(i)).data != 0;
			}

			return array;
		} else if (field.type.equals(String.class))
		{
			return tag.getString(field.name);
		} else if (field.type.equals(String[].class))
		{
			NBTTagList list = tag.getTagList(field.name);
			String[] array = new String[list.tagCount()];
			for (int i = 0; i < array.length; i++)
			{
				array[i] = ((NBTTagString) list.tagAt(i)).data;
			}

			return array;
		} else if (field.type.equals(TaggedClass.class))
		{
			NBTTagCompound compound = new NBTTagCompound();
			return readClassFromTag(compound,
					DataStorageManager.getTaggerForType(tagger
							.getTypeOfField(field.name)));
		} else
		{
			// this should never happen...
			return null;
		}
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<TaggedClass> data = new ArrayList<TaggedClass>();

		for (File file : files)
		{
			if (!file.isDirectory() && file.getName().endsWith(".dat"))
			{
				data.add(loadData(type, file.getName().replace(".dat", "")));
			}
		}

		return data.toArray(new TaggedClass[] {});
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;
		File f = getFilePath(type, uniqueObjectKey);

		if (f.exists())
		{
			isSuccess = true;
			f.delete();
		}

		return isSuccess;
	}
}
