package com.forgeessentials.multiworld.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.exception.ProviderNotFoundException;
import com.google.gson.annotations.Expose;

public class Multiworld {

    public static final String PROVIDER_DEFAULT = "default";
    public static final String PROVIDER_FLAT = "flat";
    public static final String PROVIDER_AMPLIFIED = "amp";
    public static final String PROVIDER_LARGE_BIOMES = "large";
    public static final String PROVIDER_HELL = "nether";
    public static final String PROVIDER_END = "end";
    public static final String PROVIDER_CUSTOM = "custom";
    public static final String PROVIDER_CUSTOM_HELL = "custom_nether";
    public static final String PROVIDER_CUSTOM_END = "custom_end";

    public static final WorldTypeMultiworld WORLD_TYPE_MULTIWORLD = new WorldTypeMultiworld();
    
    protected String name;

    @Expose(serialize = false)
    protected boolean worldLoaded;

    @Expose(serialize = false)
    protected boolean error;

    protected int dimensionId;

    protected String provider;

    protected List<String> biomes = new ArrayList<String>();

    @Expose(serialize = false)
    protected int providerId;

    protected long seed;

    protected GameType gameType = GameType.SURVIVAL;

    protected EnumDifficulty difficulty = EnumDifficulty.NORMAL;

    protected boolean allowHostileCreatures = true;

    protected boolean allowPeacefulCreatures = true;

    protected boolean mapFeaturesEnabled = true;

    public Multiworld(String name, String provider, long seed)
    {
        this.name = name;
        this.provider = provider;

        this.seed = seed;
        this.gameType = MinecraftServer.getServer().getGameType();
        this.difficulty = MinecraftServer.getServer().func_147135_j();
        this.allowHostileCreatures = true;
        this.allowPeacefulCreatures = true;
    }

    public Multiworld(String name, String provider)
    {
        this(name, provider, new Random().nextLong());
    }

    boolean loadWorld() throws ProviderNotFoundException
    {
        if (worldLoaded)
            return true;

        WorldType worldType = WorldType.DEFAULT;
        switch (provider.toLowerCase())
        {
        case PROVIDER_DEFAULT:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderSurface.class.getName());
            break;
        case PROVIDER_FLAT:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderSurface.class.getName());
            worldType = WorldType.FLAT;
            break;
        case PROVIDER_AMPLIFIED:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderSurface.class.getName());
            worldType = WorldType.AMPLIFIED;
            break;
        case PROVIDER_LARGE_BIOMES:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderSurface.class.getName());
            worldType = WorldType.LARGE_BIOMES;
            break;
        case PROVIDER_HELL:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderHell.class.getName());
            break;
        case PROVIDER_END:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderEnd.class.getName());
            break;
        case PROVIDER_CUSTOM:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderSurface.class.getName());
            worldType = WORLD_TYPE_MULTIWORLD;
            break;
        case PROVIDER_CUSTOM_HELL:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderHell.class.getName());
            worldType = WORLD_TYPE_MULTIWORLD;
            break;
        case PROVIDER_CUSTOM_END:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(WorldProviderEnd.class.getName());
            worldType = WORLD_TYPE_MULTIWORLD;
            break;
        default:
            providerId = ModuleMultiworld.getMultiworldManager().getProviderIDByClass(provider);
            break;
        }
        
        // Register dimension with last used id if possible
        if (DimensionManager.isDimensionRegistered(dimensionId))
        {
            dimensionId = DimensionManager.getNextFreeDimId();
        }
        DimensionManager.registerDimension(dimensionId, providerId);
        ModuleMultiworld.getMultiworldManager().worldByDim.put(dimensionId, this);

        // Initialize world settings
        MinecraftServer server = MinecraftServer.getServer();
        WorldServer overworld = DimensionManager.getWorld(0);
        if (overworld == null)
            throw new RuntimeException("Cannot hotload dim: Overworld is not Loaded!");
        ISaveHandler savehandler = new MultiworldSaveHandler(overworld.getSaveHandler(), this);
        WorldSettings worldSettings = new WorldSettings(getSeed(), getGameType(), mapFeaturesEnabled, false, worldType);

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

    public String getProvider()
    {
        return provider;
    }

    public List<String> getBiomes()
    {
        return biomes;
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