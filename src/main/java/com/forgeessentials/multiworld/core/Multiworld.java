package com.forgeessentials.multiworld.core;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.google.gson.annotations.Expose;

public class Multiworld {

//    public static class MultiworldDeserializer implements JsonDeserializer<Multiworld> {
//
//        @Override
//        public Multiworld deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
//        {
//            Multiworld multiworld = context.deserialize(json, typeOfT);
//            multiworld.worldLoaded = false;
//            multiworld.error = false;
//            try
//            {
//                multiworld.providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(multiworld.providerClass);
//            }
//            catch (ProviderNotFoundException e)
//            {
//                OutputHandler.felog.severe("Provider with name \"" + multiworld.providerClass + "\" not found!");
//                multiworld.providerClass = null;
//                multiworld.error = true;
//            }
//            return multiworld;
//        }
//
//    }

    protected String name;

    @Expose(serialize = false)
    protected boolean worldLoaded;

    @Expose(serialize = false)
    protected boolean error;

    protected int dimensionId;

    @Expose(serialize = false)
    protected int providerId;

    protected String providerClass;

    protected long seed;

    protected GameType gameType = GameType.SURVIVAL;

    protected EnumDifficulty difficulty = EnumDifficulty.NORMAL;

    protected boolean allowHostileCreatures = true;

    protected boolean allowPeacefulCreatures = true;

    Multiworld()
    {
    }

    public Multiworld(String name, String providerClass, long seed) throws ProviderNotFoundException
    {
        this.name = name;
        this.providerClass = providerClass;
        this.providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(providerClass);

        this.seed = seed;
        this.gameType = MinecraftServer.getServer().getGameType();
        this.difficulty = MinecraftServer.getServer().func_147135_j();
        this.allowHostileCreatures = true;
        this.allowPeacefulCreatures = true;

        this.loadWorld();
    }

    public Multiworld(String name, String providerClass) throws ProviderNotFoundException
    {
        this(name, providerClass, new Random().nextLong());
    }

    boolean loadWorld()
    {
        if (worldLoaded)
            return true;

        // Register dimension with last used id if possible
        if (DimensionManager.isDimensionRegistered(dimensionId))
            dimensionId = DimensionManager.getNextFreeDimId();
        DimensionManager.registerDimension(dimensionId, providerId);
        DimensionManager.initDimension(dimensionId);

        MinecraftServer server = MinecraftServer.getServer();
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null)
            throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");

        ISaveHandler savehandler = overworld.getSaveHandler();
        WorldSettings worldSettings = new WorldSettings(overworld.getWorldInfo());

        // Create WorldServer with settings
        WorldServer world = new WorldServerMultiworld(server, savehandler, //
                overworld.getWorldInfo().getWorldName(), dimensionId, worldSettings, //
                overworld, server.theProfiler, this);
        world.addWorldAccess(new WorldManager(server, world));
        // Configure world
        world.difficultySetting = difficulty;
        world.setAllowedSpawnTypes(allowHostileCreatures, allowPeacefulCreatures);

        // Post WorldEvent.Load
        MinecraftForge.EVENT_BUS.post(new WorldEvent.Load(world));
        worldLoaded = true;
        return true;
    }

    public void removeAllPlayersFromWorld()
    {
        WorldServer overworld = MinecraftServer.getServer().worldServerForDimension(0);
        MultiworldTeleporter teleporter = new MultiworldTeleporter(overworld);
        List<EntityPlayerMP> players = MinecraftServer.getServer().getConfigurationManager().playerEntityList;
        for (EntityPlayerMP player : players)
        {
            if (player.dimension == dimensionId)
            {
                teleporter.teleport(player);
            }
        }
    }

    public void updateWorldSettings()
    {
        if (!worldLoaded)
            return;
        WorldServer world = getWorld();
        world.difficultySetting = difficulty;
        world.setAllowedSpawnTypes(allowHostileCreatures, allowPeacefulCreatures);
    }

    public String getName()
    {
        return name;
    }

    public WorldServer getWorld()
    {
        return DimensionManager.getWorld(dimensionId);
    }

    public int getDimensionId()
    {
        return dimensionId;
    }

    public int getProviderId()
    {
        return providerId;
    }

    public String getProviderClass()
    {
        return providerClass;
    }

    public boolean isError()
    {
        return error;
    }

    public boolean isLoaded()
    {
        return worldLoaded;
    }

    public long getSeed()
    {
        return seed;
    }

    public GameType getGameType()
    {
        return gameType;
    }

    public void setGameType(GameType gameType)
    {
        this.gameType = gameType;
    }

    public EnumDifficulty getDifficulty()
    {
        return difficulty;
    }

    public void setDifficulty(EnumDifficulty difficulty)
    {
        this.difficulty = difficulty;
        updateWorldSettings();
    }

    public boolean isAllowHostileCreatures()
    {
        return allowHostileCreatures;
    }

    public void setAllowHostileCreatures(boolean allowHostileCreatures)
    {
        this.allowHostileCreatures = allowHostileCreatures;
        updateWorldSettings();
    }

    public boolean isAllowPeacefulCreatures()
    {
        return allowPeacefulCreatures;
    }

    public void setAllowPeacefulCreatures(boolean allowPeacefulCreatures)
    {
        this.allowPeacefulCreatures = allowPeacefulCreatures;
        updateWorldSettings();
    }

    protected void save()
    {
        DataManager.getInstance().save(this, this.name);
    }

    protected void delete()
    {
        DataManager.getInstance().delete(this.getClass(), name);
    }
    
}