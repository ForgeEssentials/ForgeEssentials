package com.forgeessentials.multiworld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.StartupQuery;

/**
 * 
 * @author Olee
 */
public class MultiworldSaveHandler implements ISaveHandler
{

    private SaveHandler parent;

    private Multiworld world;

    public MultiworldSaveHandler(ISaveHandler parent, Multiworld world)
    {
        if (!(parent instanceof SaveHandler))
            throw new RuntimeException();
        this.parent = (SaveHandler) parent;
        this.world = world;
    }

    public File getDimensionDirectory()
    {
        return new File(DimensionManager.getCurrentSaveRootDirectory(), "FEMultiworld/" + world.getName());
    }

    @Override
    public IChunkLoader getChunkLoader(WorldProvider provider)
    {
        return new AnvilChunkLoader(getDimensionDirectory(), FMLCommonHandler.instance().getMinecraftServerInstance().getDataFixer());
    }

    @Override
    public WorldInfo loadWorldInfo()
    {
        File file1 = new File(getDimensionDirectory(), "level.dat");
        if (file1.exists())
        {
            try
            {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                WorldInfo worldInfo = new WorldInfo(nbttagcompound1);
                return worldInfo;
            }
            catch (StartupQuery.AbortedException e)
            {
                throw e;
            }
            catch (Exception exception1)
            {
                exception1.printStackTrace();
            }
        }

        file1 = new File(getDimensionDirectory(), "level.dat_old");
        if (file1.exists())
        {
            try
            {
                NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
                WorldInfo worldInfo = new WorldInfo(nbttagcompound1);
                return worldInfo;
            }
            catch (StartupQuery.AbortedException e)
            {
                throw e;
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void checkSessionLock() throws MinecraftException
    {
        parent.checkSessionLock();
    }

    public void saveWorldInfoData(WorldInfo p_75755_1_, NBTTagCompound data)
    {
        NBTTagCompound dataTag = p_75755_1_.cloneNBTCompound(data);
        NBTTagCompound dataTag1 = new NBTTagCompound();
        dataTag1.setTag("Data", dataTag);

        // Save the list of mods the world was created with
        FMLCommonHandler.instance().handleWorldDataSave(parent, p_75755_1_, dataTag);

        try
        {
            File file1 = new File(getDimensionDirectory(), "level.dat_new");
            File file2 = new File(getDimensionDirectory(), "level.dat_old");
            File file3 = new File(getDimensionDirectory(), "level.dat");
            CompressedStreamTools.writeCompressed(dataTag1, new FileOutputStream(file1));

            if (file2.exists())
            {
                file2.delete();
            }
            file3.renameTo(file2);
            if (file3.exists())
            {
                file3.delete();
            }
            file1.renameTo(file3);
            if (file1.exists())
            {
                file1.delete();
            }
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo worldInfo, NBTTagCompound playerInfo)
    {
        saveWorldInfoData(worldInfo, worldInfo.cloneNBTCompound(playerInfo));
    }

    @Override
    public void saveWorldInfo(WorldInfo worldInformation)
    {
        this.saveWorldInfoWithPlayer(worldInformation, (NBTTagCompound)null);
    }

    @Override
    public IPlayerFileData getPlayerNBTManager()
    {
        return parent.getPlayerNBTManager();
    }

    @Override
    public void flush()
    {
        parent.flush();
    }

    @Override
    public File getWorldDirectory()
    {
        return getDimensionDirectory();
    }

    @Override
    public File getMapFileFromName(String name)
    {
        return parent.getMapFileFromName(name);
    }

    public TemplateManager getStructureTemplateManager()
    {
        return parent.getStructureTemplateManager();
    }

}