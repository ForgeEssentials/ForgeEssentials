package com.forgeessentials.multiworld.gen;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;

import com.forgeessentials.multiworld.WorldServerMultiworld;
import net.minecraft.world.gen.ChunkGeneratorSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.layer.GenLayer;

public class WorldTypeMultiworld extends WorldType
{

    private WorldServerMultiworld currentMultiworld;
    private WorldType world;

    public WorldTypeMultiworld(WorldType world)
    {
        super("multiworld");
        this.world = world;
    }

    public int getVersion() {
        return world.getVersion();
    }

    public WorldType getWorldTypeForGeneratorVersion(int version)
    {
        return world.getWorldTypeForGeneratorVersion(version);
    }

    /**
     * Returns true if this world Type has a version associated with it.
     */
    public boolean isVersioned()
    {
        return world.isVersioned();
    }

    public int getId() {
        return world.getId();
    }

    public net.minecraft.world.biome.BiomeProvider getBiomeProvider(World world)
    {
        return this.world.getBiomeProvider(world);
    }

    public IChunkGenerator getChunkGenerator(World world, String generatorOptions)
    {
        return this.world.getChunkGenerator(world, generatorOptions);
    }

    public int getMinimumSpawnHeight(World world)
    {
        return this.world.getMinimumSpawnHeight(world);
    }

    public double getHorizon(World world)
    {
        return this.world.getHorizon(world);
    }

    public double voidFadeMagnitude()
    {
        return this.world.voidFadeMagnitude();
    }

    public boolean handleSlimeSpawnReduction(java.util.Random random, World world)
    {
        return this.world.handleSlimeSpawnReduction(random, world);
    }

    /**
     * Called when 'Create New World' button is pressed before starting game
     */
    public void onGUICreateWorldPress() { }

    /**
     * Gets the spawn fuzz for players who join the world.
     * Useful for void world types.
     * @return Fuzz for entity initial spawn in blocks.
     */
    public int getSpawnFuzz(WorldServer world, net.minecraft.server.MinecraftServer server)
    {
        return this.world.getSpawnFuzz(world, server);
    }

    /**
     * Should world creation GUI show 'Customize' button for this world type?
     * @return if this world type has customization parameters
     */
    public boolean isCustomizable()
    {
        return this.world.isCustomizable();
    }


    /**
     * Get the height to render the clouds for this world type
     * @return The height to render clouds at
     */
    public float getCloudHeight()
    {
        return world.getCloudHeight();
    }

    /**
     * Creates the GenLayerBiome used for generating the world with the specified ChunkProviderSettings JSON String
     * *IF AND ONLY IF* this WorldType == WorldType.CUSTOMIZED.
     *
     *
     * @param worldSeed The world seed
     * @param parentLayer The parent layer to feed into any layer you return
     * @param chunkProviderSettingsJson The JSON string to use when initializing ChunkProviderSettings.Factory
     * @return A GenLayer that will return ints representing the Biomes to be generated, see GenLayerBiome
     */
    public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer, String chunkProviderSettingsJson)
    {
        return getBiomeLayer(worldSeed, parentLayer, ChunkGeneratorSettings.Factory.jsonToFactory(chunkProviderSettingsJson).build());
    }

    public GenLayer getBiomeLayer(long worldSeed, GenLayer parentLayer, ChunkGeneratorSettings chunkProviderSettings)
    {
        return this.world.getBiomeLayer(worldSeed, parentLayer, chunkProviderSettings);
    }

}
