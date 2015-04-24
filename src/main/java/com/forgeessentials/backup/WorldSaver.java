package com.forgeessentials.backup;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;

import org.apache.logging.log4j.Level;

import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class WorldSaver {
    public static String start;
    public static String done;
    public static String failed;

    private static boolean isSaving;

    private static Set<Integer> backupWorlds = new HashSet<Integer>();

    public WorldSaver()
    {
        FMLCommonHandler.instance().bus().register(this);
    }

    public static void addWorldNeedsSave(int id)
    {
        backupWorlds.add(id);
    }

    public static boolean isSaving()
    {
        return isSaving;
    }

    @SubscribeEvent
    public void tickEndEvent(TickEvent.WorldTickEvent e)
    {
        WorldServer world = (WorldServer) e.world;
        // it needs saving. save it.
        String name = world.provider.getDimensionName();
        int id = world.provider.dimensionId;
        if (backupWorlds.contains(id))
        {
            isSaving = true;
            ModuleBackup.msg(Translator.format(start, name));
            boolean bl = world.levelSaving;
            world.levelSaving = false;
            try
            {
                world.saveAllChunks(true, (IProgressUpdate) null);
            }
            catch (MinecraftException e1)
            {
                OutputHandler.felog.log(Level.ERROR, String.format(failed, name), e1);
                ModuleBackup.msg(Translator.format(failed, name));
            }
            world.levelSaving = bl;
            backupWorlds.remove(id);
            isSaving = false;
            ModuleBackup.msg(Translator.format(done, name));
        }
    }
}
