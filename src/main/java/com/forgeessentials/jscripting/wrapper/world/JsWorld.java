package com.forgeessentials.jscripting.wrapper.world;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import com.forgeessentials.jscripting.wrapper.JsWrapper;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayerList;

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
    public JsEntityPlayerList getPlayerEntities()
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

    public JsWorldServer asWorldServer()
    {
        return that instanceof WorldServer ? new JsWorldServer((WorldServer) that) : null;
    }

}
