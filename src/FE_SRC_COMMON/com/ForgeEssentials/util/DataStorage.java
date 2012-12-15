package com.ForgeEssentials.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import com.ForgeEssentials.core.ForgeEssentials;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * This needs replacing!
 * TODO Someone who understands the persistence data system, please tell me how or diy ;)
 * 
 * @author Dries007
 *
 */

public class DataStorage
{
	private static NBTTagCompound mainData;
	private static final String DATAFILENAME = "worlddata";
		
	private static final File DATAFILE = new File(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0).getChunkSaveLocation(), DATAFILENAME + ".dat");
	
	/*
	 * This class is used to store warps and other non - player based info.
	 * Don't use this to store info the user needs to be able to edit.
	 */
	
	public static NBTTagCompound getData(String name)
	{
		return mainData.getCompoundTag(name);
	}
	
	public static void setData(String name, NBTTagCompound data)
	{
		mainData.setCompoundTag(name, data);
		save();
	}
	
	public static void load()
	{
		if (DATAFILE.exists())
		{
			try
			{
				mainData = CompressedStreamTools.readCompressed(new FileInputStream(DATAFILE));
			}
			catch (FileNotFoundException e)
			{
				OutputHandler.SOP("Failed in reading file: " + DATAFILE.getName());
				e.printStackTrace();
			}
			catch (IOException e)
			{
				OutputHandler.SOP("Failed in reading file: " + DATAFILE.getName());
				e.printStackTrace();
			}
		}
		else
		{
			mainData = new NBTTagCompound();
			save();
		}
	}
	
	public static void save()
	{
		if(!DATAFILE.exists())
		{
			DATAFILE.mkdirs();
		}
		
		File var1 = new File(ForgeEssentials.FEDIR, DATAFILENAME + "_tmp_.dat");
		try
		{
			CompressedStreamTools.writeCompressed(mainData, new FileOutputStream(var1));
		} catch (FileNotFoundException e)
		{
			OutputHandler.SOP("Failed in writing file: " + DATAFILE.getName());
			e.printStackTrace();
		} catch (IOException e)
		{
			OutputHandler.SOP("Failed in writing file: " + DATAFILE.getName());
			e.printStackTrace();
		}
		
		if (DATAFILE.exists())
		{
			DATAFILE.delete();
		}

		var1.renameTo(DATAFILE);
	}
	
}
