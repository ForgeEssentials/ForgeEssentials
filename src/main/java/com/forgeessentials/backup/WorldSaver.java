package com.forgeessentials.backup;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;

import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.forgeessentials.util.OutputHandler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class WorldSaver {
    public static String start;
    public static String done;
    public static String failed;

    private static boolean isSaving;

    private static ConcurrentLinkedQueue<Integer> worlds = new ConcurrentLinkedQueue<Integer>();

    public WorldSaver()
    {
        // nthing
    }

    public static void addWorldNeedsSave(World world)
    {
        worlds.add(world.provider.dimensionId);
    }

    public static void addWorldNeedsSave(int id)
    {
        worlds.add(id);
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
        if (worlds.contains(id))
        {
            isSaving = true;
            ModuleBackup.msg(String.format(start, name));
            boolean bl = world.levelSaving;
            world.levelSaving = false;
            try
            {
                world.saveAllChunks(true, (IProgressUpdate) null);
            }
            catch (MinecraftException e1)
            {
                OutputHandler.exception(Level.SEVERE, String.format(failed, name), e1);
                ModuleBackup.msg(String.format(failed, name));
            }
            world.levelSaving = bl;

            while (worlds.remove(id))
            {
                //just do nothing and remoev them ALL
            }
            isSaving = false;
            ModuleBackup.msg(String.format(done, name));
        }
    }
}
