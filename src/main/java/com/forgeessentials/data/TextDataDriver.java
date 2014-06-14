package com.forgeessentials.data;

import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.EnumDriverType;
import com.forgeessentials.data.api.IReconstructData;
import com.forgeessentials.data.api.TypeData;
import com.forgeessentials.util.FunctionHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import java.io.File;
import java.util.ArrayList;

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
        String worldName = e.getServer().getFolderName();

        if (useFEBase)
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
