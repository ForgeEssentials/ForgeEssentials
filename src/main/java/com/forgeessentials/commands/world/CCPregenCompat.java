package com.forgeessentials.commands.world;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import cubicchunks.regionlib.impl.EntryLocation3D;
import cubicchunks.regionlib.impl.save.SaveSection3D;
import cubicchunks.regionlib.lib.ExtRegion;
import cubicchunks.regionlib.lib.provider.SimpleRegionProvider;
import io.github.opencubicchunks.cubicchunks.api.world.ICubeProviderServer.Requirement;
import io.github.opencubicchunks.cubicchunks.core.asm.mixin.ICubicWorldInternal.Server;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import io.github.opencubicchunks.cubicchunks.core.server.chunkio.SharedCachedRegionProvider;

public class CCPregenCompat
{
    private static SaveSection3D section3D;
    private static World world;
    public static SaveSection3D getSavedSection3D(World world)
    {
        if (section3D == null || CCPregenCompat.world != world)
        {
            String folder = world.provider.getSaveFolder();
            if (folder == null)
            {
                folder = ".";
            }
            Path part3d = Paths.get(folder, "region3d");

            section3D = new SaveSection3D(
                    new SharedCachedRegionProvider<>(
                            SimpleRegionProvider.createDefault(new EntryLocation3D.Provider(), part3d, 512)
                    ),
                    new SharedCachedRegionProvider<>(
                            new SimpleRegionProvider<>(new EntryLocation3D.Provider(), part3d,
                                    (keyProvider, regionKey) -> new ExtRegion<>(part3d, Collections.emptyList(), keyProvider, regionKey)
                            )
                    ));
            CCPregenCompat.world = world;
        }

        return section3D;
    }

    public static boolean isCCWorld(World world)
    {
        return world instanceof Server && ((Server) world).isCubicWorld();
    }

    public static boolean genCube(World world, IChunkProvider provider, int x, int y, int z)
    {

        CubeProviderServer providerServer = (CubeProviderServer) provider;
        Server worldInternal = (Server) world;

        try
        {
            if (getSavedSection3D(world).hasEntry(new EntryLocation3D(x, y, z)) || providerServer.getCube(x, y, z, Requirement.LOAD) != null)
            {
                return false;
            }
        } catch (IOException e) {}

        if (providerServer.getLoadedCubeCount() > 256)
        {
            providerServer.saveChunks(true);
            worldInternal.getChunkGarbageCollector().chunkGc();
        }

        providerServer.getCube(x, y, z, Requirement.LIGHT);
        return true;

    }
}
