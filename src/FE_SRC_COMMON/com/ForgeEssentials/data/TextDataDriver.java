package com.ForgeEssentials.data;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.api.data.ITaggedClass;
import com.ForgeEssentials.core.ForgeEssentials;
import com.ForgeEssentials.util.FunctionHelper;

import cpw.mods.fml.common.FMLCommonHandler;

public abstract class TextDataDriver extends DataDriver
{
	protected File	baseFile;

	@Override
	public final void parseConfigs(Configuration config, String category, String worldName) throws Exception
	{
		Property prop;

		String cat = category.substring(0, category.lastIndexOf('.'));

		prop = config.get(cat, "useFEDataDir", false);
		prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";

		boolean useFEDir = prop.getBoolean(false);

		if (useFEDir)
		{
			baseFile = new File(ForgeEssentials.FEDIR, "saves/" + getName() + "/" + worldName + "/");
		}
		else
		{
			File parent = FunctionHelper.getBaseDir();
			if (FMLCommonHandler.instance().getSide().isClient())
			{
				parent = new File(FunctionHelper.getBaseDir(), "saves/");
			}

			baseFile = new File(parent, worldName + "/FEData/" + getName() + "/");
		}

		config.save();
	}

	protected final File getTypePath(Class type)
	{
		return new File(baseFile, type.getSimpleName() + "/");
	}

	protected File getFilePath(Class type, Object uniqueKey)
	{
		return new File(getTypePath(type).getPath(), uniqueKey.toString() + "." + getExtension());
	}

	/**
	 * @return extension of the file. ommit the preceding period, its
	 *         automatically added. eg txt, cfg, dat, yml, etc...
	 */
	protected abstract String getExtension();

	@Override
	protected TaggedClass[] loadAll(Class type)
	{
		File[] files = getTypePath(type).listFiles();
		ArrayList<ITaggedClass> data = new ArrayList<ITaggedClass>();

		if (files != null)
		{
			for (File file : files)
			{
				if (!file.isDirectory() && file.getName().endsWith("." + getExtension()))
				{
					data.add(loadData(type, file.getName().replace("." + getExtension(), "")));
				}
			}
		}

		return data.toArray(new TaggedClass[] {});
	}

	@Override
	protected final boolean deleteData(Class type, Object uniqueObjectKey)
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

	@Override
	public final EnumDriverType getType()
	{
		return EnumDriverType.TEXT;
	}

}
