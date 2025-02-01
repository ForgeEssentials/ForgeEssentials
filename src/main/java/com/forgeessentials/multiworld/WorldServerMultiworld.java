package com.forgeessentials.multiworld;

import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import com.forgeessentials.core.misc.TeleportHelper.SimpleTeleporter;

public class WorldServerMultiworld extends WorldServer
{

    private SimpleTeleporter worldTeleporter;

    private Multiworld multiworld;
    public WorldServerMultiworld(MinecraftServer mcServer, ISaveHandler saveHandler, WorldInfo info, int dimensionId, WorldSettings worldSettings,
            WorldServer worldServer, Profiler profiler, Multiworld world)
    {
        super(mcServer, saveHandler, info, dimensionId, profiler);
        //        this.mapStorage = saveHandler instanceof MultiworldSaveHandler ? new MapStorage(saveHandler) : worldServer.getMapStorage();
        this.worldTeleporter = new SimpleTeleporter(this);
        this.multiworld = world;
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
        this.mapStorage.saveAllData();
        this.saveHandler.saveWorldInfo(this.worldInfo);
    }

    @Override public World init()
    {
        super.init();
        this.worldScoreboard = new MultiworldScoreboard(MinecraftServer.getServer(), this.multiworld);
        ScoreboardSaveData scoreboardsavedata = (ScoreboardSaveData) this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");

        if (scoreboardsavedata == null)
        {
            scoreboardsavedata = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", scoreboardsavedata);
        }

        scoreboardsavedata.setScoreboard(this.worldScoreboard);
        ((MultiworldScoreboard) this.worldScoreboard).func_96547_a(scoreboardsavedata);
        return this;
    }
}