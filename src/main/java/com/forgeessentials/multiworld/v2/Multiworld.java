package com.forgeessentials.multiworld.v2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.WorldUtil;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.google.gson.annotations.Expose;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class Multiworld
{
	private static String internalWorldName = "feworld";

	public static String FENameSpace = "forgeessentials";

	private String name;

	private int internalID = 0;

	private String provider;

	private String dimensionType;

	private String dimensionSetting;

	private List<String> biomes = new ArrayList<>();

	private long seed;

	private String generatorOptions;

	private GameType gameType = GameType.CREATIVE;
	private Difficulty difficulty = Difficulty.PEACEFUL;
	private boolean allowHostileCreatures =true;
	private boolean allowPeacefulCreatures = true;

	private boolean mapFeaturesEnabled = true;

	@Expose(serialize = false)
	protected boolean worldLoaded;

	@Expose(serialize = false)
	protected boolean error;

	public Multiworld(String name, String provider, String worldType, String dimensionSetting,
			long seed) {
		this(name, provider, worldType, dimensionSetting, seed, "");
	}

	public Multiworld(String name, String provider, String worldType, String dimensionSetting, long seed,
			String generatorOptions) {
		this.name = name;
		this.provider = provider;
		this.dimensionType = worldType;
		this.dimensionSetting = dimensionSetting;
		this.seed = seed;
		this.setGeneratorOptions(generatorOptions);
		this.gameType = ServerLifecycleHooks.getCurrentServer().getWorldData().getGameType();
		this.difficulty = ServerLifecycleHooks.getCurrentServer().getWorldData().getDifficulty();
		this.allowHostileCreatures = true;
		this.allowPeacefulCreatures = true;
	}

	public Multiworld(String name, String provider, String worldType, String dimensionSetting) {
		this(name, provider, worldType, dimensionSetting, new Random().nextLong());
	}

	public void removeAllPlayersFromWorld() {
		ServerWorld overworld = ServerLifecycleHooks.getCurrentServer().overworld();
		for (ServerPlayerEntity player : ServerUtil.getPlayerList()) {
			if (player.level.dimension().location().toString().equals(getResourceName())) {
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
		worldServer.setSpawnSettings(allowHostileCreatures, allowPeacefulCreatures);
	}

	public String getName() {
		return name;
	}

	public String getResourceName() {
		return FENameSpace+":"+getInternalName();
	}

	public ServerWorld getWorldServer() {
		return ServerLifecycleHooks.getCurrentServer().getLevel(getReasourceLocationUnique());
	}

	public RegistryKey<World> getReasourceLocationUnique() {
		return RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(getResourceName()));
	}

	public String getBiomeProvider() {
		return provider;
	}

	public List<String> getBiomes() {
		return biomes;
	}

	public int getInternalID() {
		return internalID;
	}
	public String getInternalName() {
		return internalWorldName+internalID;
	}

	public void setInternalID(int internalID) {
		this.internalID = internalID;
	}

	public String getDimensionSetting() {
		return dimensionSetting;
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

	public String getGeneratorOptions() {
		return generatorOptions;
	}

	public void setGeneratorOptions(String generatorOptions) {
		this.generatorOptions = generatorOptions;
	}

	public String getDimensionType() {
		return dimensionType;
	}

	protected void save() {
		DataManager.getInstance().save(this, getInternalName());
	}

	protected void delete() {
		DataManager.getInstance().delete(this.getClass(), getInternalName());
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
		if (worldChange)
			displayDepartMessage(player);

		y = WorldUtil.placeInWorld(world, (int) x, (int) y, (int) z);
		WarpPoint target = new WarpPoint(world.dimension().location().toString(), x, y, z,
				player.yRot, player.xRot);
		if (instant)
			TeleportHelper.checkedTeleport(player, target);
		else
			TeleportHelper.doTeleport(player, target);

		if (worldChange)
			displayWelcomeMessage(player);
	}

	public static void displayDepartMessage(ServerPlayerEntity player) {
		String msg = "Leaving " + " (" + player.level.dimension().location().getPath() + ")";
		ChatOutputHandler.sendMessage(player, msg);
	}

	public static void displayWelcomeMessage(ServerPlayerEntity player) {
		String msg = "Entering " + " (" + player.level.dimension().location().getPath() + ")";
		ChatOutputHandler.sendMessage(player, msg);
	}
}