package com.forgeessentials.multiworld;

/**
 * 
 * @author Olee
 */
public class MultiworldSaveHandler //implements ISaveHandler
{
/*
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
                CompoundNBT nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                CompoundNBT nbttagcompound1 = nbttagcompound.getCompound("Data");
                IWorldInfo worldInfo = new WorldInfo(nbttagcompound1);
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
                CompoundNBT nbttagcompound = CompressedStreamTools.readCompressed(new FileInputStream(file1));
                CompoundNBT nbttagcompound1 = nbttagcompound.getCompound("Data");
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

    public void saveWorldInfoData(WorldInfo p_75755_1_, CompoundNBT data)
    {
        CompoundNBT dataTag = p_75755_1_.cloneNBTCompound(data);
        CompoundNBT dataTag1 = new CompoundNBT();
        dataTag1.put("Data", dataTag);

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
    public void saveWorldInfoWithPlayer(WorldInfo worldInfo, CompoundNBT playerInfo)
    {
        saveWorldInfoData(worldInfo, worldInfo.cloneNBTCompound(playerInfo));
    }

    @Override
    public void saveWorldInfo(WorldInfo worldInformation)
    {
        this.saveWorldInfoWithPlayer(worldInformation, (CompoundNBT) null);
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
*/
}