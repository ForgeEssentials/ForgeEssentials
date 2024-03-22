package com.forgeessentials.multiworld.v2.genWorld;

import java.util.List;
import java.util.concurrent.Executor;

import net.minecraft.server.MinecraftServer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.portal.PortalForcer;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.CustomSpawner;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.LevelStorageSource;

public class ServerWorldMultiworld extends ServerLevel
{

	private PortalForcer worldTeleporter;

	public ServerWorldMultiworld(MinecraftServer mcServer, 
			Executor executor, LevelStorageSource.LevelStorageAccess levelSave, 
			ServerLevelData derivedworldinfo, ResourceKey<Level> worldKey, 
			DimensionType type, ChunkProgressListener chunkListener, 
			ChunkGenerator chunkGenerator, boolean debug, long seed, 
			List<CustomSpawner> spawn, boolean tick) {
		super(mcServer, executor, levelSave, derivedworldinfo, worldKey, type, chunkListener, chunkGenerator, debug, seed, spawn, tick);
		this.worldTeleporter = new PortalForcer(this);
	}

	@Override
	public PortalForcer getPortalForcer() {
		return this.worldTeleporter;
	}
}