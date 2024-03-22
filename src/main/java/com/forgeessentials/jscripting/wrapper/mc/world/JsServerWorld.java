package com.forgeessentials.jscripting.wrapper.mc.world;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class JsServerWorld extends JsWorld<ServerLevel>
{

    public static JsServerWorld getServerWorld(String dim)
    {
        return JsWorld.get(dim);
    }

    /**
     * Sets the world time.
     */
    public JsServerWorld(ServerLevel that)
    {
        super(that);
    }

    public void setWorldTime(long time)
    {
        that.setDayTime(time);
    }

    public void setSpawnLocation(int x, int y, int z)
    {
        that.setDefaultSpawnPos(new BlockPos(x, y, z), 0.0F);
    }
}
