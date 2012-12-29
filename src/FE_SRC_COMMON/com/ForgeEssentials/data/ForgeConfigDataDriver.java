package com.ForgeEssentials.data;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.data.TaggedClass.SavedField;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

/**
 * Storage driver for filesystem (flat-file) persistence.
 * 
 * @author AbrarSyed
 *
 */
public class ForgeConfigDataDriver extends DataDriver
{
	private File baseFile;
	
	@Override
	public void parseConfigs(Configuration config, String worldName)
	{
		Property prop;
		
		prop = config.get("Data.ForgeConfig", "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";
		
		boolean useFEDir = prop.getBoolean(false);
		
		if (useFEDir)
		{
			this.baseFile = new File(ForgeEssentials.FEDIR , "saves/ForgeConfig/" + worldName + "/");
		}
		else
		{
			File parent = FunctionHelper.getBaseDir();
			if (FMLCommonHandler.instance().getSide().isClient())
				parent = new File(FunctionHelper.getBaseDir(), "saves/");
			
			this.baseFile = new File(parent, worldName + "/FEData/ForgeConfig/");
		}
		
		config.save();
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
		
		File file = this.getFilePath(type, objectData.uniqueKey.value);
		
		// Wipe existing Forge Configuration file - they don't take new data.
		if (file.exists())
		{
			file.delete();
		}
		
		Configuration cfg = new Configuration(file, true);
		
		for (SavedField field : objectData.TaggedMembers.values())
			writeFieldToProperty(cfg, type.getSimpleName(), field);
		
		cfg.save();
		
		return wasSuccessful;
	}
	
	@Override
	protected TaggedClass loadData(Class type, Object uniqueKey)
	{
		Configuration cfg = new Configuration(getFilePath(type, uniqueKey), true);
		TypeTagger tag = DataStorageManager.getTaggerForType(type);
		
		return readClassFromProperty(cfg, cfg.categories.get(type.getSimpleName()), tag);
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
	
	private void writeFieldToProperty(Configuration cfg, String category, SavedField field)
	{
		if (field == null || field.type == null)
		{
			// ignore.
		}
		else if (field.type.equals(Integer.class))
		{
			cfg.get(category, field.name, ((Integer)field.value).intValue());
		}
		else if (field.type.equals(int[].class))
		{
			cfg.get(category, field.name, (int[])field.value);
		}
		else if (field.type.equals(Float.class) || field.type.equals(Double.class))
		{
			cfg.get(category, field.name, ((Double)field.value).doubleValue());
		}
		else if (field.type.equals(double[].class))
		{
			cfg.get(category, field.name, (double[])field.value);
		}
		else if (field.type.equals(Boolean.class))
		{
			cfg.get(category, field.name, ((Boolean)field.value).booleanValue());
		}
		else if (field.type.equals(boolean[].class))
		{
			cfg.get(category, field.name, (boolean[])field.value);
		}
		else if (field.type.equals(String.class))
		{
			cfg.get(category, field.name, (String)field.value);
		}
		else if (field.type.equals(String[].class))
		{
			cfg.get(category, field.name, (String[])field.value);
		}
		else if (field.type.equals(TaggedClass.class))
		{
			TaggedClass tag = (TaggedClass) field.value;
			String newcat = category+"."+field.name;
			
			for (SavedField f : tag.TaggedMembers.values())
				writeFieldToProperty(cfg, newcat, f);
		}
		else
		{
			throw new IllegalArgumentException("Cannot save object type.");
		}
	}
	
	private Object readFieldFromProperty(Configuration cfg, String category, SavedField field)
	{
		if (field.type.equals(Integer.class))
		{
			return cfg.get(category, field.name, 0).getInt();
		}
		else if (field.type.equals(int[].class))
		{
			return cfg.get(category, field.name, new int[] {}).getIntList();
		}
		else if (field.type.equals(Float.class) || field.type.equals(Double.class))
		{
			return cfg.get(category, field.name, 0d).getDouble(0);
		}
		else if (field.type.equals(double[].class))
		{
			return cfg.get(category, field.name, new double[] {}).getDoubleList();
		}
		else if (field.type.equals(Boolean.class))
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
		else  // this should never happen...
			return null;
	}
	
	private TaggedClass readClassFromProperty(Configuration cfg, ConfigCategory cat, TypeTagger tag)
	{
		TaggedClass data = new TaggedClass();

		if (cat != null)
		{
			SavedField field;
	
			for (Property prop : cat.getValues().values())
			{
				if (tag.isUniqueKeyField && prop.getName().equals(tag.uniqueKey))
				{
					field = data.new SavedField();
					field.name = tag.uniqueKey;
					field.type = tag.getTypeOfField(field.name);
					field.value = readFieldFromProperty(cfg, cat.getQualifiedName(), field);
					data.uniqueKey = field;
					continue;
				}
				
				field = data.new SavedField();
				field.name = prop.getName();
				field.type = tag.getTypeOfField(field.name);
				field.value = readFieldFromProperty(cfg, cat.getQualifiedName(), field);
				data.addField(field);
			}
			
			for (ConfigCategory child : cfg.categories.values())
			{
				if (child.isChild() && child.parent == cat)  // intentional use of ==
				{
					field = data.new SavedField();
					field.name = child.getQualifiedName().replace(cat.getQualifiedName()+".", "");
					field.type = tag.getTypeOfField(field.name);
					field.value = readClassFromProperty(cfg, child, DataStorageManager.getTaggerForType(field.type));
					data.addField(field);
				}
			}
		}
		
		return data;
	}
}
