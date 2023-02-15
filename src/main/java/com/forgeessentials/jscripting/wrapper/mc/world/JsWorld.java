package com.forgeessentials.jscripting.wrapper.mc.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntity;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityList;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayerList;
import com.forgeessentials.jscripting.wrapper.mc.util.JsAxisAlignedBB;

/**
 * @tsd.static World
 */
public class JsWorld<T extends World> extends JsWrapper<T>
{

    private static Map<World, JsWorld<?>> worldCache = new WeakHashMap<>();

    /**
     * @tsd.ignore
     */
    public static JsWorld<?> get(World world)
    {
        if (worldCache.containsKey(world))
            return worldCache.get(world);
        JsWorld<?> jsWorld = new JsWorld<>(world);
        worldCache.put(world, jsWorld);
        return jsWorld;

    }

    public static JsServerWorld get(String dim)
    {
        ServerWorld world = ServerLifecycleHooks.getCurrentServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(dim)));
        return world == null ? null : new JsServerWorld(world);
    }

    protected Map<TileEntity, JsTileEntity<?>> tileEntityCache = new WeakHashMap<>();

    protected JsWorld(T that)
    {
        super(that);
    }

    public String getDimension()
    {
        return that.dimension().location().toString();
    }

    public int getDifficulty()
    {
        return that.getDifficulty().ordinal();
    }

    public JsEntityPlayerList getPlayerEntities()
    {
        List<PlayerEntity> players = new ArrayList<>();
        for (ServerPlayerEntity player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            players.add((PlayerEntity) player);
        }
        return new JsEntityPlayerList(players);
    }

    // TODO: this should take an entity type somehow
    public JsEntityList getEntitiesWithinAABB(JsAxisAlignedBB axisAlignedBB)
    {
        return new JsEntityList(that.getEntitiesOfClass(Entity.class, axisAlignedBB.getThat()));
    }

    public boolean blockExists(int x, int y, int z)
    {
        return !that.isEmptyBlock(new BlockPos(x, y, z));
    }

    public JsBlock getBlock(int x, int y, int z)
    {
        return JsBlock.get(that.getBlockState(new BlockPos(x, y, z)).getBlock());
    }

    public void setBlock(int x, int y, int z, JsBlock block)
    {
        that.setBlock(new BlockPos(x, y, z), block.getThat().defaultBlockState(), 3);
    }

    public void setBlock(int x, int y, int z, JsBlock block, int meta)
    {
        that.setBlock(new BlockPos(x, y, z), block.getThat().stateById(meta), 3);
    }

    public JsTileEntity<?> getTileEntity(int x, int y, int z)
    {
        TileEntity tileEntity = that.getBlockEntity(new BlockPos(x, y, z));
        if (tileEntityCache.containsKey(tileEntity))
            return tileEntityCache.get(tileEntity);
        JsTileEntity<?> jsTileEntity = new JsTileEntity<>(tileEntity);
        tileEntityCache.put(tileEntity, jsTileEntity);
        return jsTileEntity;
    }

    public JsServerWorld asWorldServer()
    {
        return that instanceof ServerWorld ? new JsServerWorld((ServerWorld) that) : null;
    }

    public long getWorldTime()
    {
        return that.getDayTime();
    }

    public long getTotalWorldTime()
    {
        return that.getGameTime();
    }

    /**
     * Called when checking if a certain block can be mined or not. The 'spawn safe zone' check is located here.
     */
    public boolean canMineBlock(JsEntityPlayer player, int x, int y, int z)
    {
        return that.mayInteract(player.getThat(), new BlockPos(x, y, z));
    }

    public float getWeightedThunderStrength(float weight)
    {
        return that.getThunderLevel(weight);
    }

    /**
     * Not sure about this actually. Reverting this one myself.
     */
    public float getRainStrength(float strength)
    {
        return that.getRainLevel(strength);
    }

    /**
     * Returns true if the current thunder strength (weighted with the rain strength) is greater than 0.9
     */
    public boolean isThundering()
    {
        return that.isThundering();
    }

    /**
     * Returns true if the current rain strength is greater than 0.2
     */
    public boolean isRaining()
    {
        return that.isRaining();
    }

    public boolean canLightningStrikeAt(int x, int y, int z)
    {
        return that.isRainingAt(new BlockPos(x, y, z));
    }

    /**
     * Checks to see if the biome rainfall values for a given x,y,z coordinate set are extremely high
     */
    public boolean isBlockHighHumidity(int x, int y, int z)
    {
        return that.isHumidAt(new BlockPos(x, y, z));
    }

    /**
     * Returns current world height.
     */
    public int getHeight()
    {
        return that.getHeight();
    }

    /**
     * Returns current world height.
     */
    public int getActualHeight()
    {
        return that.getMaxBuildHeight();
    }

    public JsBlock getTopBlock(int x, int z)
    {
        return JsBlock.get(that.getBlockState(new BlockPos(x,getHeightValue(x,z), z)).getBlock());
    }

    /**
     * Returns true if the block at the specified coordinates is empty
     */
    public boolean isAirBlock(int x, int y, int z)
    {
        return that.isEmptyBlock(new BlockPos(x, y, z));
    }

    /**
     * Checks if the specified block is able to see the sky
     */
    public boolean canBlockSeeTheSky(int x, int y, int z)
    {
        return that.canSeeSky(new BlockPos(x, y, z));
    }

    /**
     * Gets the light value of a block location
     */
    public int getBlockLightValue(int x, int y, int z)
    {
        return that.getMaxLocalRawBrightness(new BlockPos(x, y, z));
    }

    /**
     * Gets the light value of a block location. This is the actual function that gets the value and has a bool flag that indicates if its a half step block to get the maximum
     * light value of a direct neighboring block (left, right, forward, back, and up)
     */
    public int getBlockLightValue_do(int x, int y, int z, boolean isHalfBlock)
    {
        return that.getLightEmission(new BlockPos(x, y, z));//, isHalfBlock);
    }//Half Slabs have no different properties anymore?

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHeightValue(int x, int z)
    {
        return that.getHeight(Heightmap.Type.WORLD_SURFACE_WG, x, z);
    }

    /**
     * Returns how bright the block is shown as which is the block's light value looked up in a lookup table (light values aren't linear for brightness). Args: x, y, z
     */
    public float getLightBrightness(int x, int y, int z)
    {
        return that.getBrightness(new BlockPos(x, y, z));
    }

    /**
     * Checks whether its daytime by seeing if the light subtracted from the skylight is less than 4
     */
    public boolean isDaytime()
    {
        return that.isDay();
    }

    /**
     * calls calculateCelestialAngle
     */
    public float getCelestialAngle(float arg1)
    {
        return that.getSunAngle(arg1);
    }

    /**
     * gets the current fullness of the moon expressed as a float between 1.0 and 0.0, in steps of .25
     */
    public float getCurrentMoonPhaseFactor()
    {
        return that.getMoonPhase();
    }

    /**
     * Return getCelestialAngle() * 2 * PI
     */
    public float getCelestialAngleRadians(float arg1)
    {
        return that.getSunAngle(arg1) * (float)( 2 * Math.PI);
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     */
    public JsEntityPlayer getClosestPlayerToEntity(JsEntity<?> entity, double dist)
    {
        return JsEntityPlayer.get(that.getNearestPlayer(entity.getThat(), dist));
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not limit the distance).
     */
    public JsEntityPlayer getClosestPlayer(double x, double y, double z, double dist)
    {
        return JsEntityPlayer.get(that.getNearestPlayer(x, y, z, dist, false));
    }

    /**
     * Retrieve the world seed from level.dat
     */
    public long getSeed()
    {
        return ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed();
    }

    // /**
    // * Sets the strength of the rain.
    // */
    // @SideOnly(Side.CLIENT)
    // public void setRainStrength(float strength)
    // {
    // that.setRainStrength(strength);
    // }
    //
    // /**
    // * Sets the strength of the thunder.
    // */
    // @SideOnly(Side.CLIENT)
    // public void setThunderStrength(float strength)
    // {
    // that.setThunderStrength(strength);
    // }
    //
    // /**
    // * Returns horizon height for use in rendering the sky.
    // */
    // @SideOnly(Side.CLIENT)
    // public double getHorizon()
    // {
    // return that.getHorizon();
    // }
    //
    // /**
    // * Returns the sun brightness - checks time of day, rain and thunder
    // */
    // @SideOnly(Side.CLIENT)
    // public float getSunBrightness(float p_72971_1_)
    // {
    // return that.getSunBrightness(p_72971_1_);
    // }
    //
    // @SideOnly(Side.CLIENT)
    // public float getSunBrightnessBody(float p_72971_1_)
    // {
    // return that.getSunBrightnessBody(p_72971_1_);
    // }
    //
    // @SideOnly(Side.CLIENT)
    // public int getMoonPhase()
    // {
    // return that.getMoonPhase();
    // }
    //
    // /**
    // * Returns the name of the current chunk provider, by calling chunkprovider.makeString()
    // */
    // @SideOnly(Side.CLIENT)
    // public String getProviderName()
    // {
    // return that.getProviderName();
    // }
}
