package com.ForgeEssentials.data.filesystem;

import java.io.File;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.TaggedClass.SavedField;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * 
 * @author MysteriousAges
 *
 */
public class FileSystemDataDriver extends DataDriver
{

	public static String driverType = "FileSystem";
	
	private HashMap<Class, String> filePaths;
	
	private String baseFilePath;
	private static String newline = "\r\n";
	
	public FileSystemDataDriver()
	{
		this.filePaths = new HashMap<Class, String>();
	}
	
	@Override
	public boolean parseConfigs(Configuration config, String worldName)
	{
		Property prop;
		
		prop = config.get("Data.FileSystem", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFilePath = ForgeEssentials.FEDIR.toString() + "saves/" + worldName + "/";
		}
		else
		{
			try
			{
				Class c = Class.forName("net.minecraft.src.IntegratedServer");
				// We are running from the client. Use the Client save directory.
				this.baseFilePath = "./saves/" + worldName + "/FEData/";
			}
			catch (Exception e)
			{
				// Dedicated server. Use the base path + world name.
				this.baseFilePath = "./" + worldName +"/FEData/";
			}
		}
		
		config.save();
		
		// Nothing to fail on.
		return true;
	}
	
	private String getFilePath(Class type, Object loadingKey)
	{
		String path = this.baseFilePath + type.getSimpleName() + "/";
		
		if (loadingKey instanceof String)
		{
			path = path + loadingKey;
		}
		else
		{
			path = path + loadingKey.toString();
		}
		
		return path + ".cfg";
	}

	@Override
	protected boolean saveData(Class type, TaggedClass objectData)
	{
		boolean wasSuccessful = false;
		
		File file = new File(this.getFilePath(type, objectData.LoadingKey.Value));
		
		// Wipe existing Forge Configuration file - they don't take new data.
		if (file.exists())
		{
			file.delete();
		}
		
		Configuration cfg = new Configuration(file);
		
		this.saveFieldToProperty(cfg, objectData.LoadingKey.FieldName, objectData.LoadingKey);
		
		TaggedClass.SavedField[] fieldList = objectData.TaggedMembers.toArray(new TaggedClass.SavedField[objectData.TaggedMembers.size()]);
		
		this.saveFields(cfg, "", fieldList);
		
		cfg.save();
		
		return wasSuccessful;
	}

	private void saveFields(Configuration cfg, String parentName, SavedField[] fieldList)
	{
		String tagPrefix;
		if (parentName != null && parentName.length() > 0)
		{
			tagPrefix = parentName + ".";
		}
		else
		{
			tagPrefix = "";
		}
		for (TaggedClass.SavedField field : fieldList)
		{
			if (field.Value instanceof TaggedClass)
			{
				// Nested classes SHOULD NOT have a loading field.
				TaggedClass innerObject = (TaggedClass)field.Value;
				TaggedClass.SavedField[] fields = innerObject.TaggedMembers.toArray(new TaggedClass.SavedField[innerObject.TaggedMembers.size()]);
				this.saveFields(cfg, tagPrefix + field.FieldName, fields);
			}
			else
			{
				this.saveFieldToProperty(cfg, tagPrefix + field.FieldName, field);
			}
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
		boolean isSuccess = false;
		File f = new File(this.getFilePath(type, uniqueObjectKey));
		
		if (f.exists())
		{
			isSuccess = true;
			f.delete();
		}
		
		return isSuccess;
	}
	
	private void saveFieldToProperty(Configuration cfg, String category, TaggedClass.SavedField field)
	{
		if (field.Type == Integer.class)
		{
			cfg.get(category, "value", ((Integer)field.Value).intValue());
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == int[].class)
		{
			cfg.get(category, "value", (int[])field.Value);
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == Float.class || field.Type == Double.class)
		{
			cfg.get(category, "value", ((Double)field.Value).doubleValue());
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == double[].class)
		{
			cfg.get(category, "value", (double[])field.Value);
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == Boolean.class)
		{
			cfg.get(category, "value", ((Boolean)field.Value).booleanValue());
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == boolean[].class)
		{
			cfg.get(category, "value", (boolean[])field.Value);
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == String.class)
		{
			cfg.get(category, "value", (String)field.Value);
			cfg.get(category, "type", field.Type.getName());
		}
		else if (field.Type == String[].class)
		{
			cfg.get(category, "value", (String[])field.Value);
			cfg.get(category, "type", field.Type.getName());
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}
}
