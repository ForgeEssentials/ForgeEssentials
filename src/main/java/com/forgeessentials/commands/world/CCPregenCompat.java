package com.forgeessentials.commands.world;

import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;

import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer;
import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer.Requirement;
import io.github.opencubicchunks.cubicchunks.api.world.ICubicWorld;

public class CCPregenCompat
{

    public static boolean isCCWorld(World world)
    {
        return world instanceof ICubicWorld && ((ICubicWorld) world).isCubicWorld();
    }

    public static boolean genCube(World world, AbstractChunkProvider provider, int x, int y, int z)
    {

        ICubeProviderServer providerServer = (ICubeProviderServer) provider;
        if (providerServer.isCubeGenerated(x, y, z))
        {
            return false;
        }

        providerServer.getCube(x, y, z, Requirement.LIGHT);
        return true;

    }
}
