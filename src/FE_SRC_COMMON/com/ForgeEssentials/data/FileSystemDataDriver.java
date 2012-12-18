package com.ForgeEssentials.data;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.TaggedClass.SavedField;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * 
 * @author MysteriousAges
 *
 */
public class FileSystemDataDriver extends DataDriver
{

	public static String driverType = "FlatFile_ForgeConfig";
	
	private HashMap<Class, String> filePaths;
	
	private File baseFile;
	private static final String NEWLINE = Configuration.NEW_LINE;
	
	public FileSystemDataDriver()
	{
		this.filePaths = new HashMap<Class, String>();
	}
	
	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		Property prop;
		
		prop = config.get("Data.FlatFile", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFile = new File(ForgeEssentials.FEDIR , "saves/" + worldName + "/");
		}
		else
		{
			File parent = new File("./saves/");
			if (FMLCommonHandler.instance().getSide().isServer())
				parent = new File(".");
			
			this.baseFile = new File(parent, worldName + "/FEData/");
		}
		
		config.save();
		
		// Nothing to fail on.
		return true;
	}
	
	private File getTypePath(Class type)
	{
		return new File(baseFile, type.getSimpleName()+"/");
	}
	
	private File getFilePath(Class type, Object uniqueKey)
	{
		return new File(getTypePath(type), uniqueKey.toString()+".cfg");
	}

	@Override
	protected boolean saveData(Class type, TaggedClass objectData)
	{
		boolean wasSuccessful = false;
		
		File file = this.getFilePath(type, objectData.LoadingKey);
		
		// Wipe existing Forge Configuration file - they don't take new data.
		if (file.exists())
		{
			file.delete();
		}
		
		Configuration cfg = new Configuration(file, true);
		
		for (SavedField field : objectData.TaggedMembers.values())
			saveFieldToProperty(cfg, type.getSimpleName(), field);
		
		cfg.save();
		
		return wasSuccessful;
	}
	
	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		Configuration cfg = new Configuration(getFilePath(type, uniqueKey), true);
		
		
		return null;
	}

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<TaggedClass> data = new ArrayList<TaggedClass>();
		
		for (File file : files)
			if (!file.isDirectory() && file.getName().endsWith(".cfg"))
				data.add(loadData(type, file.getName().replace(".cfg", "")));
		
		return data.toArray(new TaggedClass[] {});
	}

	@Override
	protected boolean deleteData(Class type, Object uniqueObjectKey)
	{
		boolean isSuccess = false;
		File f = this.getFilePath(type, uniqueObjectKey);
		
		if (f.exists())
		{
			isSuccess = true;
			f.delete();
		}
		
		return isSuccess;
	}
	
	private void saveFieldToProperty(Configuration cfg, String category, SavedField field)
	{
		if (field == null || field.Type == null)
		{
			// ignore.
		}
		else if (field.Type.equals(Integer.class))
		{
			cfg.get(category, field.FieldName, ((Integer)field.Value).intValue());
		}
		else if (field.Type.equals(int[].class))
		{
			cfg.get(category, field.FieldName, (int[])field.Value);
		}
		else if (field.Type.equals(Float.class) || field.Type.equals(Double.class))
		{
			cfg.get(category, field.FieldName, ((Double)field.Value).doubleValue());
		}
		else if (field.Type.equals(double[].class))
		{
			cfg.get(category, field.FieldName, (double[])field.Value);
		}
		else if (field.Type.equals(Boolean.class))
		{
			cfg.get(category, field.FieldName, ((Boolean)field.Value).booleanValue());
		}
		else if (field.Type.equals(boolean[].class))
		{
			cfg.get(category, field.FieldName, (boolean[])field.Value);
		}
		else if (field.Type.equals(String.class))
		{
			cfg.get(category, field.FieldName, (String)field.Value);
		}
		else if (field.Type.equals(String[].class))
		{
			cfg.get(category, field.FieldName, (String[])field.Value);
		}
		else if (field.Type.equals(TaggedClass.class))
		{
			TaggedClass tag = (TaggedClass) field.Value;
			String newcat = category+"."+tag.Type.getSimpleName();
			
			for (SavedField f : tag.TaggedMembers.values())
				saveFieldToProperty(cfg, newcat, f);
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}
	
	private void readFieldFromProperty(Configuration cfg, String category, SavedField field)
	{
		if (field.Type.equals(Integer.class))
		{
			cfg.get(category, field.FieldName, 0);
		}
		else if (field.Type.equals(int[].class))
		{
			cfg.get(category, field.FieldName, new int[] {});
		}
		else if (field.Type.equals(Float.class) || field.Type.equals(Double.class))
		{
			cfg.get(category, field.FieldName, 0d);
		}
		else if (field.Type.equals(double[].class))
		{
			cfg.get(category, field.FieldName, new double[] {});
		}
		else if (field.Type.equals(Boolean.class))
		{
			cfg.get(category, field.FieldName, false);
		}
		else if (field.Type.equals(boolean[].class))
		{
			cfg.get(category, field.FieldName, new boolean[] {});
		}
		else if (field.Type.equals(String.class))
		{
			cfg.get(category, field.FieldName, "");
		}
		else if (field.Type.equals(String[].class))
		{
			cfg.get(category, field.FieldName, new String[] {});
		}
		else if (field.Type.equals(TaggedClass.class))
		{
			// TODO: change
			TaggedClass tag = (TaggedClass) field.Value;
			String newcat = category+"."+tag.Type.getSimpleName();
			
			for (SavedField f : tag.TaggedMembers.values())
				saveFieldToProperty(cfg, newcat, f);
		}
	}
}
