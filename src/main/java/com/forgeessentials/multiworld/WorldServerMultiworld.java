package com.forgeessentials.multiworld;

import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import com.forgeessentials.core.misc.TeleportHelper.SimpleTeleporter;

public class WorldServerMultiworld extends WorldServer
{

    private SimpleTeleporter worldTeleporter;

    public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, WorldInfo info, int dimensionId, WorldSettings worldSettings,
            WorldServer worldServer, Profiler profiler, Multiworld world)
    {
        super(mcServer, saveHandler, info, dimensionId, profiler);
        this.mapStorage = worldServer.getMapStorage();
        this.worldScoreboard = worldServer.getScoreboard();
        this.worldTeleporter = new SimpleTeleporter(this);
    }

    @Override
    public Teleporter getDefaultTeleporter()
    {
        return this.worldTeleporter;
    }

    @Override
    protected void saveLevel() throws MinecraftException
    {
        this.perWorldStorage.saveAllData();
        this.saveHandler.saveWorldInfo(this.worldInfo);
    }

}