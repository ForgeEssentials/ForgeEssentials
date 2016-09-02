package com.forgeessentials.jscripting.wrapper.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.forgeessentials.jscripting.wrapper.JsAxisAlignedBB;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityList;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayerList;

public class JsWorld<T extends World> extends JsWrapper<T>
{
    private static Map<World, JsWorld<?>> worldCache = new WeakHashMap<>();
    protected Map<TileEntity, JsTileEntity<?>> tileEntityCache = new WeakHashMap<>();

    protected JsWorld(T that)
    {
        super(that);
    }

    public int getDimension()
    {
        return that.provider.getDimension();
    }

    public int getDifficulty()
    {
        return that.getDifficulty().ordinal();
    }

    @SuppressWarnings("unchecked")
    public JsEntityPlayerList getPlayerEntities()
    {
        return new JsEntityPlayerList(that.playerEntities);
    }

    // TODO: this should take an entity type somehow
    @SuppressWarnings("unchecked")
    public JsEntityList getEntitiesWithinAABB(JsAxisAlignedBB axisAlignedBB)
    {
        return new JsEntityList(that.getEntitiesWithinAABB(Entity.class, axisAlignedBB.getThat()));
    }

    public boolean blockExists(int x, int y, int z)
    {
        return !that.isBlockLoaded(new BlockPos(x, y, z));
    }

    public JsBlock getBlock(int x, int y, int z)
    {
        return JsBlock.get(that.getBlockState(new BlockPos(x, y, z)).getBlock());
    }

    public void setBlock(int x, int y, int z, JsBlock block)
    {
        that.setBlockState(new BlockPos(x, y, z), block.getThat().getDefaultState());
    }

    public void setBlock(int x, int y, int z, JsBlock block, int meta)
    {
        that.setBlockState(new BlockPos(x, y, z), block.getThat().getStateFromMeta(meta));
    }

    public JsTileEntity<?> getTileEntity(int x, int y, int z)
    {
        TileEntity tileEntity = that.getTileEntity(new BlockPos(x, y, z));
        if (tileEntityCache.containsKey(tileEntity))
            return tileEntityCache.get(tileEntity);
        JsTileEntity<?> jsTileEntity = new JsTileEntity<>(tileEntity);
        tileEntityCache.put(tileEntity, jsTileEntity);
        return jsTileEntity;
    }

    public JsWorldServer asWorldServer()
    {
        return that instanceof WorldServer ? new JsWorldServer((WorldServer) that) : null;
    }

    public static JsWorld<?> get(World world)
    {
        if (worldCache.containsKey(world))
            return worldCache.get(world);
        JsWorld<?> jsWorld = new JsWorld<>(world);
        worldCache.put(world, jsWorld);
        return jsWorld;

    }

}
