package com.forgeessentials.multiworld.v2;

import com.mojang.serialization.Codec;

import net.minecraft.block.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.Blockreader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.SingleBiomeProvider;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.WorldGenRegion;
import net.minecraft.world.gen.feature.structure.StructureManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;

public class MultiworldChunkGenerator  extends ChunkGenerator
{
	// create generator on runtime by dynamic generation
	public MultiworldChunkGenerator(MinecraftServer server)
	{
		this(server.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY));
	}

	// create generator on server init from file
	public MultiworldChunkGenerator(Registry<Biome> biomes)
	{
		super(new SingleBiomeProvider(biomes.getOrThrow(Biomes.PLAINS)), new DimensionStructuresSettings(false));
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
	public void buildSurfaceAndBedrock(WorldGenRegion p_225551_1_, IChunk p_225551_2_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fillFromNoise(IWorld p_230352_1_, StructureManager p_230352_2_, IChunk p_230352_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBaseHeight(int p_222529_1_, int p_222529_2_, Type p_222529_3_) {
		return 0;
	}

	@Override
	public IBlockReader getBaseColumn(int p_230348_1_, int p_230348_2_) {
		return new Blockreader(new BlockState[0]);
	}
}
