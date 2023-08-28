package com.forgeessentials.multiworld.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Multiworld
{
    
	protected String name;

	protected RegistryKey<World> dimensionId;

	protected String provider;

	protected String worldType;

	protected List<String> biomes = new ArrayList<>();

	protected long seed;

	protected String generatorOptions;

	protected GameType gameType = GameType.CREATIVE;
	protected Difficulty difficulty = Difficulty.PEACEFUL;
	protected boolean allowHostileCreatures =true;
	protected boolean allowPeacefulCreatures = true;

	protected boolean mapFeaturesEnabled = true;

	@Expose(serialize = false)
	protected boolean worldLoaded;

	@Expose(serialize = false)
	protected boolean error;

	@Expose(serialize = false)
	protected int providerId;

	@Expose(serialize = false)
	protected DimensionType worldTypeObj;

	public Multiworld(String name, String provider, String worldType,
			long seed) {
		this(name, provider, worldType, seed, "");
	}

	public Multiworld(String name, String provider, String worldType, long seed,
			String generatorOptions) {
		this.name = name;
		this.provider = provider;
		this.worldType = worldType;

		this.seed = seed;
		this.generatorOptions = generatorOptions;
		this.gameType = ServerLifecycleHooks.getCurrentServer().getWorldData().getGameType();
		this.difficulty = ServerLifecycleHooks.getCurrentServer().getWorldData().getDifficulty();
		this.allowHostileCreatures = true;
		this.allowPeacefulCreatures = true;
	}

	public Multiworld(String name, String provider, String worldType) {
		this(name, provider, worldType, new Random().nextLong());
	}

	public void removeAllPlayersFromWorld() {
		ServerWorld overworld = ServerLifecycleHooks.getCurrentServer().overworld();
		for (ServerPlayerEntity player : ServerUtil.getPlayerList()) {
			if (player.level.dimension().equals(dimensionId)) {
				BlockPos playerPos = player.blockPosition();
				int y = WorldUtil.placeInWorld(player.level, playerPos.getX(),
						playerPos.getY(), playerPos.getZ());
				WarpPoint point = new WarpPoint(overworld, playerPos.getX(), y,
						playerPos.getZ(), 0, 0);
				TeleportHelper.doTeleport(player, point);
			}
		}
	}

	public void updateWorldSettings() {
		if (!worldLoaded)
			return;
		ServerWorld worldServer = getWorldServer();
		//worldServer.getCurrentDifficultyAt(null);
		worldServer.setSpawnSettings(allowHostileCreatures, allowPeacefulCreatures);
	}

	public String getName() {
		return name;
	}

	public ServerWorld getWorldServer() {
		return ServerLifecycleHooks.getCurrentServer().getLevel(dimensionId);
	}

	public RegistryKey<World> getDimensionId() {
		return dimensionId;
	}

	public int getProviderId() {
		return providerId;
	}

	public String getProvider() {
		return provider;
	}

	public List<String> getBiomes() {
		return biomes;
	}

	public boolean isError() {
		return error;
	}

	public boolean isLoaded() {
		return worldLoaded;
	}

	public long getSeed() {
		return seed;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public Difficulty getDifficulty() {
		return difficulty;
	}

	public void setDifficulty(Difficulty difficulty) {
		this.difficulty = difficulty;
		updateWorldSettings();
	}

	public boolean isAllowHostileCreatures() {
		return allowHostileCreatures;
	}

	public void setAllowHostileCreatures(boolean allowHostileCreatures) {
		this.allowHostileCreatures = allowHostileCreatures;
		updateWorldSettings();
	}

	public boolean isAllowPeacefulCreatures() {
		return allowPeacefulCreatures;
	}

	public void setAllowPeacefulCreatures(boolean allowPeacefulCreatures) {
		this.allowPeacefulCreatures = allowPeacefulCreatures;
		updateWorldSettings();
	}

	protected void save() {
		DataManager.getInstance().save(this, this.name);
	}

	protected void delete() {
		DataManager.getInstance().delete(this.getClass(), name);
	}
     

    /**
     * Teleport the player to the multiworld
     * 
     */
	public void teleport(ServerPlayerEntity player, boolean instant) {
		teleport(player, getWorldServer(), instant);
	}

    /**
     * Teleport the player to the multiworld
     * 
     */
	public static void teleport(ServerPlayerEntity player, ServerWorld world,
			boolean instant) {
		teleport(player, world, player.position().x, player.position().y,
				player.position().x, instant);
	}

    /**
	 * Teleport the player to the multiworld
	 * 
	 */
	public static void teleport(ServerPlayerEntity player, ServerWorld world,
			double x, double y, double z, boolean instant) {
		boolean worldChange = player.level.dimension() != world.dimension();
		//if (worldChange)
		//	displayDepartMessage(player);

		y = WorldUtil.placeInWorld(world, (int) x, (int) y, (int) z);
		WarpPoint target = new WarpPoint(world.dimension().location().toString(), x, y, z,
				player.yRot, player.xRot);
		if (instant)
			TeleportHelper.checkedTeleport(player, target);
		else
			TeleportHelper.teleport(player, target);

		//if (worldChange)
		//	displayWelcomeMessage(player);
	}

//	public static void displayDepartMessage(ServerPlayerEntity player) {
//		String msg = player.level.provider.getDepartMessage();
//		if (msg == null)
//			msg = "Leaving the Overworld.";
//		if (player.dimension > 1 || player.dimension < -1)
//			msg += " (#" + player.dimension + ")";
//		ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
//	}
//
//	public static void displayWelcomeMessage(ServerPlayerEntity player) {
//		String msg = player.world.provider.getWelcomeMessage();
//		if (msg == null)
//			msg = "Entering the Overworld.";
//		if (player.dimension > 1 || player.dimension < -1)
//			msg += " (#" + player.dimension + ")";
//		ChatOutputHandler.sendMessage(player, new ChatComponentText(msg));
//	}
}