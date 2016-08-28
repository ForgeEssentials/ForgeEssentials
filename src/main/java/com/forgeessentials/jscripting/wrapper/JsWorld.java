package com.forgeessentials.jscripting.wrapper;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.forgeessentials.util.MappedList;

public class JsWorld<T extends World> extends JsWrapper<T>
{

    public JsWorld(T that)
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
    public MappedList<EntityPlayer, JsEntityPlayer> getPlayerEntities()
    {
        return new JsEntityPlayerList(that.playerEntities);
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

    // public void get() // tsgen ignore
    // {
    // return that.;
    // }

}
