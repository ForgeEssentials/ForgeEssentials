package com.forgeessentials.multiworld.v2;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.DimensionType;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.chunk.listener.IChunkStatusListener;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.ISpecialSpawner;
import net.minecraft.world.storage.IServerWorldInfo;
import net.minecraft.world.storage.SaveFormat;

public class WorldServerMultiworld extends ServerWorld
{

	private Teleporter worldTeleporter;

	public WorldServerMultiworld(MinecraftServer mcServer, 
			Executor executor, SaveFormat.LevelSave levelSave, 
			IServerWorldInfo derivedworldinfo, RegistryKey<World> worldKey, 
			DimensionType type, IChunkStatusListener chunkListener, 
			ChunkGenerator chunkGenerator, boolean debug, long seed, 
			List<ISpecialSpawner> spawn, boolean tick) {
		super(mcServer, executor, levelSave, derivedworldinfo, worldKey, type, chunkListener, chunkGenerator, debug, seed, spawn, tick);
		this.worldTeleporter = new Teleporter(this);
	}

	@Override
	public Teleporter getPortalForcer() {
		return this.worldTeleporter;
	}
}