package com.forgeessentials.multiworld.v2.genWorld;

import com.mojang.serialization.Codec;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryLookupCodec;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
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
	public void fillFromNoise(LevelAccessor p_230352_1_, StructureFeatureManager p_230352_2_, ChunkAccess p_230352_3_) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBaseHeight(int p_222529_1_, int p_222529_2_, Types p_222529_3_) {
		return 0;
	}

	@Override
	public BlockGetter getBaseColumn(int p_230348_1_, int p_230348_2_) {
		return new NoiseColumn(new BlockState[0]);
	}
}
