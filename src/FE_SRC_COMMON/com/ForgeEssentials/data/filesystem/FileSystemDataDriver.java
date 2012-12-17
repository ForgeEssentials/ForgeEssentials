package com.ForgeEssentials.data.filesystem;

import java.io.File;
import java.util.HashMap;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.DataDriver;
import com.ForgeEssentials.data.TaggedClass;
import com.ForgeEssentials.data.TypeTagger;
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
		
		prop = config.get("Data.FileSystem", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFile = new File(ForgeEssentials.FEDIR , "saves/" + worldName + "/");
		}
		else
		{
			try
			{
				Class c = Class.forName("net.minecraft.src.IntegratedServer");
				// We are running from the client. Use the Client save directory.
				this.baseFile = new File("./saves/" + worldName + "/FEData/");
			}
			catch (Exception e)
			{
				// Dedicated server. Use the base path + world name.
				this.baseFile = new File("./" + worldName +"/FEData/");
			}
		}
		
		config.save();
		
		// Nothing to fail on.
		return true;
	}
	
	private File getFilePath(Class type, Object loadingKey)
	{
		return new File(baseFile, type.getSimpleName() + "/"+loadingKey.toString()+".cfg");
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
}
