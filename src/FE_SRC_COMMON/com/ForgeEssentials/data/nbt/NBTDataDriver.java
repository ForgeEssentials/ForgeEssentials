package com.ForgeEssentials.data.nbt;

import java.io.File;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.TaggedClass.SavedField;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Side;

public class NBTDataDriver extends DataDriver
{
	private String baseFilePath;

	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		Property prop;

		prop = config.get("Data.NBT", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";

		boolean useFEDir = prop.getBoolean(false);

		if (useFEDir)
		{
			this.baseFilePath = ForgeEssentials.FEDIR.toString() + "saves/" + worldName + "/";
		}
		else
		{
			if (Side.CLIENT == FMLCommonHandler.instance().getEffectiveSide())
			{
				this.baseFilePath = "./saves/" + worldName + "/";
			}
			else
			{
				this.baseFilePath = "./" + worldName + "/";
			}
		}

		config.save();

		// Nothing to fail on.
		return true;
	}

	public String getBaseBath()
	{
		return this.baseFilePath;
	}

	@Override
	protected boolean saveData(Class type, TaggedClass fieldList)
	{
		boolean successful = true;
		
		// Create file object
		File file = new File(this.baseFilePath + fieldList.LoadingKey.toString() + ".dat");
		
		if (file.exists())
		{
			// Delete, since we're saving the entire file every time anyhow.
			file.delete();
		}
		
		NBTTagCompound dataFile = new NBTTagCompound();
		
		for (SavedField field : fieldList.TaggedMembers.values())
		{
			
		}
		
		return successful;
	}
	
	private void saveObjectInTag(NBTTagCompound tag, SavedField field)
	{
		if (field == null || field.Type == null)
		{
			// ignore.
		}
		else if (field.Type.equals(Integer.class))
		{
			tag.setInteger(field.FieldName, (Integer)field.Value);
		}
		else if (field.Type.equals(int[].class))
		{
			tag.setIntArray(field.FieldName, (int[])field.Value);
		}
		else if (field.Type.equals(Float.class))
		{
			tag.setFloat(field.FieldName, (Float)field.Value);
		}
		else if (field.Type.equals(Double.class))
		{
			tag.setDouble(field.FieldName, (Double)field.Value);
		}
		else if (field.Type.equals(double[].class))
		{
		}
		else if (field.Type.equals(Boolean.class))
		{
		}
		else if (field.Type.equals(boolean[].class))
		{
		}
		else if (field.Type.equals(String.class))
		{
		}
		else if (field.Type.equals(String[].class))
		{
		}
		else if (field.Type.equals(TaggedClass.class))
		{
			// recurse
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}

	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean successful = true;
		return successful;
	}
}
