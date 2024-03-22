package com.forgeessentials.multiworld.v2.genWorld;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.levelgen.StructureSettings;
//not used rn
public class MultiworldChunkGenerator extends ChunkGenerator
{
	// create generator on runtime by dynamic generation
	public MultiworldChunkGenerator(MinecraftServer server)
	{
		this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
	}

	// create generator on server init from file
	public MultiworldChunkGenerator(Registry<Biome> biomes)
	{
		super(new FixedBiomeSource(biomes.getOrThrow(Biomes.PLAINS)), new StructureSettings(false));
		this.biomes = biomes;
	}
	
	// this Codec will need to be registered to the chunk generator registry in Registry
	// during FMLCommonSetupEvent::enqueueWork
	// (unless and until a forge registry wrapper becomes made for chunk generators)
	public static final Codec<MultiworldChunkGenerator> CODEC =
		RegistryLookupCodec.create(Registry.BIOME_REGISTRY).xmap(MultiworldChunkGenerator::new,MultiworldChunkGenerator::getBiomeRegistry)
		.codec();
	
	private final Registry<Biome> biomes;	
	public Registry<Biome> getBiomeRegistry() { 
		return this.biomes;
	}

	@Override
	protected Codec<? extends ChunkGenerator> codec() {
		return CODEC;
	}

	@Override
	public ChunkGenerator withSeed(long p_230349_1_) {
		return this;
	}

	@Override
	public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, ChunkAccess p_225551_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_156171_, StructureFeatureManager p_156172_, ChunkAccess p_156173_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBaseHeight(int p_156153_, int p_156154_, Types p_156155_, LevelHeightAccessor p_156156_) {
		return 0;
	}

	@Override
	public NoiseColumn getBaseColumn(int p_156150_, int p_156151_, LevelHeightAccessor p_156152_) {
		return new NoiseColumn(1, new BlockState[0]);
	}
}
