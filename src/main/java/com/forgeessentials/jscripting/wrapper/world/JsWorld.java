package com.forgeessentials.jscripting.wrapper.world;

import java.util.Map;
import java.util.WeakHashMap;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityList;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayerList;
import com.forgeessentials.jscripting.wrapper.util.JsAxisAlignedBB;

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

    public static JsWorldServer get(int dim)
    {
        WorldServer world = DimensionManager.getWorld(dim);
        return world == null ? null : new JsWorldServer(world);
    }

    protected Map<TileEntity, JsTileEntity<?>> tileEntityCache = new WeakHashMap<>();

    protected JsWorld(T that)
    {
        super(that);
    }

    public int getDimension()
    {
        return that.provider.dimensionId;
    }

    public int getDifficulty()
    {
        return that.difficultySetting.ordinal();
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
        return that.blockExists(x, y, z);
    }

    public JsBlock getBlock(int x, int y, int z)
    {
        return JsBlock.get(that.getBlock(x, y, z));
    }

    public void setBlock(int x, int y, int z, JsBlock block)
    {
        that.setBlock(x, y, z, block.getThat());
    }

    public void setBlock(int x, int y, int z, JsBlock block, int meta)
    {
        that.setBlock(x, y, z, block.getThat(), meta, 3);
    }

    public JsTileEntity<?> getTileEntity(int x, int y, int z)
    {
        TileEntity tileEntity = that.getTileEntity(x, y, z);
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

}
