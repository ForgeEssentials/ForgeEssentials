package com.forgeessentials.commands.world;

import java.io.IOException;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer.Requirement;
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal.Server;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;

public class CCPregenCompat
{

    public static boolean isCCWorld(World world)
    {
        return world instanceof Server && ((Server) world).isCubicWorld();
    }

    public static boolean genCube(World world, IChunkProvider provider, int x, int y, int z)
    {

        CubeProviderServer providerServer = (CubeProviderServer) provider;
        Server worldInternal = (Server) world;
        if (providerServer.isCubeGenerated(x,y,z))
        {
            return false;
        }

        if (providerServer.getLoadedCubeCount() > 256)
        {
            providerServer.saveChunks(true);
            //TODO: Replace/update this line with code that replicates the functionality of providerServer.queueUnloadAll() in CC
            //worldInternal.getChunkGarbageCollector().chunkGc();
        }

        providerServer.getCube(x, y, z, Requirement.LIGHT);
        return true;

    }
}
