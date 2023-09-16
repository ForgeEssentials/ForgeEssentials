package com.forgeessentials.jscripting.wrapper.mc.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

public class JsServerWorld extends JsWorld<ServerWorld>
{

    public static JsServerWorld getServerWorld(String dim)
    {
        return JsWorld.get(dim);
    }

    /**
     * Sets the world time.
     */
    public JsServerWorld(ServerWorld that)
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
