package com.forgeessentials.jscripting.wrapper.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.forgeessentials.jscripting.wrapper.JsAxisAlignedBB;
import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityList;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayerList;

public class JsWorld<T extends World> extends JsWrapper<T>
{

    public JsWorld(T that)
    {
        super(that);
    }

    public int getDimension()
    {
        return that.provider.getDimensionId();
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

    public JsWorldServer asWorldServer()
    {
        return that instanceof WorldServer ? new JsWorldServer((WorldServer) that) : null;
    }

}
