package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.TickTask;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.EnderDragonPartEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.server.ServerWorld;

public class CommandButcherTickTask implements TickTask {

	public static enum ButcherMobType {
		ALL, HOSTILE, PASSIVE, VILLAGER, TAMABLE, TAMED, GOLEM, BOSS;

		public static List<String> getNames() {
			List<String> result = new ArrayList<>();
			for (ButcherMobType type : ButcherMobType.values())
				result.add(type.name());
			return result;
		}
	}

	private CommandSource sender;
	private ButcherMobType mobType;
	private AxisAlignedBB aabb;
	private ServerWorld world;
	private int radius;

	private int maxChunkX;
	private int maxChunkZ;
	private int minChunkX;
	private int minChunkZ;
	private int killCount;
	private int tickKillCount;

	private static final int MAX_TICK_KILLS = 1;

	public CommandButcherTickTask(CommandSource sender, ServerWorld world, ButcherMobType mobType, AxisAlignedBB aabb,
			int radius) {
		this.sender = sender;
		this.mobType = mobType;
		this.radius = radius;
		this.world = world;
		if (radius > -1) {
			this.aabb = aabb;
			minChunkX = MathHelper.floor((aabb.minX - world.getMaxEntityRadius()) / 16.0D);
			maxChunkX = MathHelper.floor((aabb.maxX + world.getMaxEntityRadius()) / 16.0D);
			minChunkZ = MathHelper.floor((aabb.minZ - world.getMaxEntityRadius()) / 16.0D);
			maxChunkZ = MathHelper.floor((aabb.maxZ + world.getMaxEntityRadius()) / 16.0D);
		}
	}

	public CommandButcherTickTask(CommandSource sender, ServerWorld world, String mobType, AxisAlignedBB aabb,
			int radius) {
		this(sender, world, CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase()), aabb, radius);
	}

	public static void schedule(CommandSource sender, ServerWorld world, String mobType, AxisAlignedBB aabb, int radius)
			throws CommandException {
		try {
			ButcherMobType mobT = CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase());
			TaskRegistry.schedule(new CommandButcherTickTask(sender, world, mobT, aabb, radius));
		} catch (IllegalArgumentException e) {
			ChatOutputHandler.chatError(sender, "Unknown mob type. Mob types are "
					+ StringUtils.join(CommandButcherTickTask.ButcherMobType.values(), ", "));
			return;
		}
	}

	@Override
	public boolean tick() {
		tickKillCount = 0;
		if (radius < -1)
			return true;
		else if (radius == -1) {
			for (Object entity : world.getAllEntities())
				if (entity instanceof LivingEntity) {
					checkEntity((LivingEntity) entity);
					if (tickKillCount >= 32)
						return false;
				}
			ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
		} else {
			for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX)
				for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ)
					if (world.getChunk(chunkX, chunkZ) != null) {
						List<LivingEntity> list = new LinkedList<>();
						world.getChunk(chunkX, chunkZ).getEntitiesOfClass(LivingEntity.class, aabb, list, null);
						for (LivingEntity entity : list) {
							checkEntity(entity);
							if (tickKillCount >= MAX_TICK_KILLS)
								return false;
						}
					}
			ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
		}
		return true;
	}

	private void checkEntity(LivingEntity entity) {
		if (shouldKill(entity)) {
			killEntity(entity);
			killCount++;
			tickKillCount++;
		}
	}

	private boolean shouldKill(LivingEntity entity) {
		String className = entity.getClass().getName();
		switch (mobType) {
		case ALL:
			return true;
		case HOSTILE:
			if (entity instanceof MobEntity || entity instanceof GhastEntity)
				return true;
			if (entity instanceof SlimeEntity && ((SlimeEntity) entity).getSize() > 0)
				return true;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.HOSTILE).contains(className))
				return true;
			return false;
		case PASSIVE:
			// Filter out tamed creatures
			if (entity instanceof TameableEntity && ((TameableEntity) entity).isTame())
				return false;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE).contains(className)
					&& MobTypeRegistry.isTamed(entity))
				return false;
			// Check for other creatures
			if (entity instanceof AnimalEntity || entity instanceof AmbientEntity || entity instanceof SquidEntity)
				return true;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.PASSIVE).contains(className))
				return true;
			return false;
		case VILLAGER:
			if (entity instanceof VillagerEntity)
				return true;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.VILLAGER).contains(className))
				return true;
			return false;
		case TAMABLE:
			if (entity instanceof TameableEntity)
				return true;
			return false;
		case TAMED:
			if (entity instanceof TameableEntity && ((TameableEntity) entity).isTame())
				return true;
			return false;
		case GOLEM:
			if (entity instanceof GolemEntity)
				return true;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.GOLEM).contains(className))
				return true;
			return false;
		case BOSS:
			if (entity instanceof EnderDragonEntity || entity instanceof WitherEntity)
				return true;
			if (MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS).contains(className))
				return true;
		default:
			return false;
		}
	}

	private static void killEntity(Entity entity) {
		if (entity instanceof EnderDragonEntity) {
			for (EnderDragonPartEntity part : ((EnderDragonEntity) entity).getSubEntities())
				part.remove();
		}
		entity.kill();
	}

	@Override
	public boolean editsBlocks() {
		return false;
	}

}
