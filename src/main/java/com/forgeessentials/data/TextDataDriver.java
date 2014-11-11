package com.forgeessentials.data;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.EnumDriverType;
import com.forgeessentials.commons.IReconstructData;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.util.FunctionHelper;

import cpw.mods.fml.common.event.FMLServerStartingEvent;

public abstract class TextDataDriver extends AbstractDataDriver {
    protected File baseFile;
    private boolean useFEBase = false;

    @Override
    public final void loadFromConfigs(Configuration config, String category) throws Exception
    {
        Property prop;

        String cat = category.substring(0, category.lastIndexOf('.'));

        prop = config.get(cat, "useFEDataDir", false);
        prop.comment = "Set to true to use the '.minecraft/ForgeEssentials/saves' directory instead of a world. Server owners may wish to set this to true.";

        useFEBase = prop.getBoolean(false);

        config.save();
    }

    @Override
    public final void serverStart(FMLServerStartingEvent e)
    {
        if (useFEBase)
            baseFile = new File(ForgeEssentials.getFEDirectory(), "saves/" + e.getServer().getFolderName() + "/" + getName());
        else
            baseFile = new File(FunctionHelper.getWorldPath(), "FEData/" + getName());
    }

    protected final File getTypePath(ClassContainer type)
    {
        return new File(baseFile, type.getFileSafeName() + "/");
    }

    protected File getFilePath(ClassContainer type, String uniqueKey)
    {
        return new File(getTypePath(type).getPath(), uniqueKey + "." + getExtension());
    }

    /**
     * @return extension of the file. omit the preceding period, its
     * automatically added. eg txt, cfg, dat, yml, etc...
     */
    protected abstract String getExtension();

    @Override
    protected TypeData[] loadAll(ClassContainer type)
    {
        File[] files = getTypePath(type).listFiles();
        ArrayList<IReconstructData> data = new ArrayList<IReconstructData>();

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

        return data.toArray(new TypeData[] { });
    }

    @Override
    protected final boolean deleteData(ClassContainer type, String uniqueKey)
    {
        boolean isSuccess = false;
        File f = getFilePath(type, uniqueKey);

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
