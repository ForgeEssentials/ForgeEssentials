package com.forgeessentials.multiworld;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.command.CommandException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.common.FMLCommonHandler;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.google.gson.annotations.Expose;

/**
 * 
 * @author Olee
 */
public class Multiworld
{

    protected String name;

    protected int dimensionId;

    protected String provider;

    protected String worldType;

    protected List<String> biomes = new ArrayList<>();

    protected long seed;

    protected String generatorOptions;

    // protected GameType gameType = GameType.CREATIVE;
    //
    // protected EnumDifficulty difficulty = EnumDifficulty.PEACEFUL;
    //
    // protected boolean allowHostileCreatures = true;
    //
    // protected boolean allowPeacefulCreatures = true;

    protected boolean mapFeaturesEnabled = true;

    protected boolean loadOnServerStart = true;

    @Expose(serialize = false)
    protected boolean worldLoaded;

    @Expose(serialize = false)
    protected boolean error;

    @Expose(serialize = false)
    protected int providerId;

    @Expose(serialize = false)
    protected WorldType worldTypeObj;

    public Multiworld(String name, String provider, String worldType, long seed) {
        this(name, provider, worldType, seed, "");
    }

    public Multiworld(String name, String provider, String worldType, long seed, String generatorOptions)
    {
        this.name = name;
        this.provider = provider;
        this.worldType = worldType;

        this.seed = seed;
        this.generatorOptions = generatorOptions;
        // this.gameType = MinecraftServer.getServer().getGameType();
        // this.difficulty = MinecraftServer.getServer().func_147135_j();
        // this.allowHostileCreatures = true;
        // this.allowPeacefulCreatures = true;
    }

    public Multiworld(String name, String provider, String worldType)
    {
        this(name, provider, worldType, new Random().nextLong());
    }

    private Multiworld() {
        //This is intentionally empty and only exists so gson will fill in the default values specified in the class!
    }

    public void removeAllPlayersFromWorld()
    {
        WorldServer overworld = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(0);
        for (EntityPlayerMP player : ServerUtil.getPlayerList())
        {
            if (player.dimension == dimensionId)
            {
                BlockPos playerPos = player.getPosition();
                int y = WorldUtil.placeInWorld(player.worldObj, playerPos.getX(), playerPos.getY(), playerPos.getZ());
                WarpPoint point = new WarpPoint(overworld, playerPos.getX(), y, playerPos.getZ(), 0, 0);
                TeleportHelper.doTeleport(player, point);
            }
        }
    }

    public void updateWorldSettings()
    {
        if (!worldLoaded)
            return;
        // WorldServer worldServer = getWorldServer();
        // worldServer.difficultySetting = difficulty;
        // worldServer.setAllowedSpawnTypes(allowHostileCreatures, allowPeacefulCreatures);
    }

    public String getName()
    {
        return name;
    }

    public WorldServer getWorldServer()
    {
        return MinecraftServer.getServer().worldServerForDimension(dimensionId);
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

    // public GameType getGameType()
    // {
    // return gameType;
    // }
    //
    // public void setGameType(GameType gameType)
    // {
    // this.gameType = gameType;
    // }
    //
    // public EnumDifficulty getDifficulty()
    // {
    // return difficulty;
    // }
    //
    // public void setDifficulty(EnumDifficulty difficulty)
    // {
    // this.difficulty = difficulty;
    // updateWorldSettings();
    // }
    //
    // public boolean isAllowHostileCreatures()
    // {
    // return allowHostileCreatures;
    // }
    //
    // public void setAllowHostileCreatures(boolean allowHostileCreatures)
    // {
    // this.allowHostileCreatures = allowHostileCreatures;
    // updateWorldSettings();
    // }
    //
    // public boolean isAllowPeacefulCreatures()
    // {
    // return allowPeacefulCreatures;
    // }
    //
    // public void setAllowPeacefulCreatures(boolean allowPeacefulCreatures)
    // {
    // this.allowPeacefulCreatures = allowPeacefulCreatures;
    // updateWorldSettings();
    // }

    protected void save()
    {
        DataManager.getInstance().save(this, this.name);
    }

    protected void delete()
    {
        DataManager.getInstance().delete(this.getClass(), name);
    }

    /**
     * Teleport the player to the multiworld
     */
    public void teleport(EntityPlayerMP player, boolean instant) throws CommandException
    {
        teleport(player, getWorldServer(), instant);
    }

    /**
     * Teleport the player to the multiworld
     */
    public static void teleport(EntityPlayerMP player, WorldServer world, boolean instant) throws CommandException
    {
        teleport(player, world, player.posX, player.posY, player.posZ, instant);
    }

    /**
     * Teleport the player to the multiworld
     */
    public static void teleport(EntityPlayerMP player, WorldServer world, double x, double y, double z, boolean instant) throws CommandException
    {
        boolean worldChange = player.worldObj.provider.getDimensionId() != world.provider.getDimensionId();
        if (worldChange)
            displayDepartMessage(player);

        y = WorldUtil.placeInWorld(world, (int) x, (int) y, (int) z);
        WarpPoint target = new WarpPoint(world.provider.getDimensionId(), x, y, z, player.rotationPitch, player.rotationYaw);
        if (instant)
            TeleportHelper.checkedTeleport(player, target);
        else
            TeleportHelper.teleport(player, target);

        if (worldChange)
            displayWelcomeMessage(player);
    }

    public static void displayDepartMessage(EntityPlayerMP player)
    {
        // String msg = player.worldObj.provider.getDepartMessage();
        // if (msg == null)
        // msg = "Leaving the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

    public static void displayWelcomeMessage(EntityPlayerMP player)
    {
        // String msg = player.worldObj.provider.getWelcomeMessage();
        // if (msg == null)
        // msg = "Entering the Overworld.";
        // if (player.dimension > 1 || player.dimension < -1)
        // msg += " (#" + player.dimension + ")";
        // ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
    }

}