package com.forgeessentials.jscripting.wrapper.mc.world;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntity;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityList;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntity;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsPlayerEntityList;
import com.forgeessentials.jscripting.wrapper.mc.util.JsAxisAlignedBB;
import com.forgeessentials.util.ServerUtil;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fmllegacy.server.ServerLifecycleHooks;
import net.minecraft.server.level.ServerLevel;

/**
 * @tsd.static World
 */
public class JsWorld<T extends Level> extends JsWrapper<T>
{

    private static Map<Level, JsWorld<?>> worldCache = new WeakHashMap<>();

    /**
     * @tsd.ignore
     */
    public static JsWorld<?> get(Level world)
    {
        if (worldCache.containsKey(world))
            return worldCache.get(world);
        JsWorld<?> jsWorld = new JsWorld<>(world);
        worldCache.put(world, jsWorld);
        return jsWorld;

    }

    public static JsServerWorld get(String dim)
    {
        ServerLevel world = ServerUtil.getWorldFromString(dim);
        return world == null ? null : new JsServerWorld(world);
    }

    protected Map<BlockEntity, JsTileEntity<?>> tileEntityCache = new WeakHashMap<>();

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

    public JsPlayerEntityList getPlayerEntities()
    {
        List<Player> players = new ArrayList<>();
        for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers())
        {
            players.add((Player) player);
        }
        return new JsPlayerEntityList(players);
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
        that.setBlock(new BlockPos(x, y, z), block.getThat().defaultBlockState(), 3);
    }

    public JsTileEntity<?> getTileEntity(int x, int y, int z)
    {
        BlockEntity tileEntity = that.getBlockEntity(new BlockPos(x, y, z));
        if (tileEntityCache.containsKey(tileEntity))
            return tileEntityCache.get(tileEntity);
        JsTileEntity<?> jsTileEntity = new JsTileEntity<>(tileEntity);
        tileEntityCache.put(tileEntity, jsTileEntity);
        return jsTileEntity;
    }

    public JsServerWorld asWorldServer()
    {
        return that instanceof ServerLevel ? new JsServerWorld((ServerLevel) that) : null;
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
    public boolean canMineBlock(JsPlayerEntity player, int x, int y, int z)
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
        return JsBlock.get(that.getBlockState(new BlockPos(x, getHeightValue(x, z), z)).getBlock());
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
        return that.getLightEmission(new BlockPos(x, y, z));// , isHalfBlock);
    }// Half Slabs have no different properties anymore?

    /**
     * Returns the y coordinate with a block in it at this x, z coordinate
     */
    public int getHeightValue(int x, int z)
    {
        return that.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
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
        return that.getSunAngle(arg1) * (float) (2 * Math.PI);
    }

    /**
     * Gets the closest player to the entity within the specified distance (if distance is less than 0 then ignored).
     */
    public JsPlayerEntity getClosestPlayerToEntity(JsEntity<?> entity, double dist)
    {
        return JsPlayerEntity.get(that.getNearestPlayer(entity.getThat(), dist));
    }

    /**
     * Gets the closest player to the point within the specified distance (distance can be set to less than 0 to not limit the distance).
     */
    public JsPlayerEntity getClosestPlayer(double x, double y, double z, double dist)
    {
        return JsPlayerEntity.get(that.getNearestPlayer(x, y, z, dist, false));
    }

    /**
     * Retrieve the world seed from level.dat
     */
    public long getSeed()
    {
        return ServerLifecycleHooks.getCurrentServer().getWorldData().worldGenSettings().seed();
    }
}
