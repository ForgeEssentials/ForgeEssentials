package com.ForgeEssentials.commands.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.command.ICommandSender;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import com.ForgeEssentials.api.commands.EnumMobType;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.ITickTask;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;

public class CommandButcherTickTask implements ITickTask
{
	double			MAX_ENTITY_RADIUS;
	boolean			isComplete;
	AxisAlignedBB	aabb;
	EntityPlayer	player;
	ICommandSender	sender;
	boolean			playerCommand;
	String			mobType;
	int				counter, radius, tempCount;
	int				var4, var5, var6, var7;
	int				var9temp, var10temp;
	World			world;

	public CommandButcherTickTask(EntityPlayer sender, String mobType, AxisAlignedBB aabb, int radius)
	{
		player = sender;
		this.sender = sender;
		this.mobType = mobType;
		this.radius = radius;
		if (radius == -1)
		{

		}
		else
		{
			this.aabb = aabb;
			var4 = var9temp = MathHelper.floor_double((aabb.minX - MAX_ENTITY_RADIUS) / 16.0D);
			var5 = MathHelper.floor_double((aabb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
			var6 = var10temp = MathHelper.floor_double((aabb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
			var7 = MathHelper.floor_double((aabb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
		}
		world = sender.worldObj;
		counter = 0;
		playerCommand = true;
		tempCount = 0;
		MAX_ENTITY_RADIUS = World.MAX_ENTITY_RADIUS;
	}

	public CommandButcherTickTask(ICommandSender sender, String mobType, AxisAlignedBB aabb, int radius, int worldID)
	{
		this.sender = sender;
		this.mobType = mobType;
		this.radius = radius;
		if (radius == -1)
		{

		}
		else
		{
			this.aabb = aabb;
			var4 = var9temp = MathHelper.floor_double((aabb.minX - MAX_ENTITY_RADIUS) / 16.0D);
			var5 = MathHelper.floor_double((aabb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
			var6 = var10temp = MathHelper.floor_double((aabb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
			var7 = MathHelper.floor_double((aabb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
		}
		world = FunctionHelper.getDimension(worldID);
		counter = 0;
		playerCommand = false;
		tempCount = 0;
	}

	@Override
	public void tick()
	{
		if (radius == -1)
		{
			for (Object entity : world.loadedEntityList)
			{
				if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer))
				{
					if (mobType.equalsIgnoreCase("hostile") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.HOSTILE);
						if (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast)
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (mobType.equalsIgnoreCase("passive") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.PASSIVE);
						if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid)
						{
							if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
							{
								continue;
							}
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (mobType.equalsIgnoreCase("villager") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.VILLAGER);
						Set<String> tameableSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE);
						if (entity instanceof EntityVillager)
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (mobType.equalsIgnoreCase("golem") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.GOLEM);
						if (entity instanceof EntityGolem)
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (mobType.equalsIgnoreCase("tamed") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE);
						if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (mobType.equalsIgnoreCase("boss") || mobType.equalsIgnoreCase("all"))
					{
						Set<String> typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS);
						if (entity instanceof EntityDragon)
						{
							for (EntityDragonPart part : ((EntityDragon) entity).dragonPartArray)
							{
								part.setDead();
							}
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (entity instanceof EntityWither)
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
						else if (typeSet.contains(entity.getClass().getName()))
						{
							((EntityLiving) entity).setDead();
							counter++;
							tempCount++;
						}
					}
					if (tempCount == 29)
					{
						tempCount = 0;
						return;
					}
				}
			}
			isComplete = true;
		}
		for (int var9 = var9temp; var9 <= var5; ++var9)
		{
			for (int var10 = var10temp; var10 <= var7; ++var10)
			{
				if (world.getChunkProvider().chunkExists(var9, var10))
				{
					List<EntityLiving> list = new LinkedList<EntityLiving>();
					world.getChunkFromChunkCoords(var9, var10).getEntitiesOfTypeWithinAAAB(EntityLiving.class, aabb, list, (IEntitySelector) null);
					for (EntityLiving entity : list)
					{
						if (mobType.equalsIgnoreCase("hostile") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast)
							{
								entity.setDead();
								counter++;
								tempCount++;
							}
						}
						if (mobType.equalsIgnoreCase("passive") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature)
							{
								if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
								{
									continue;
								}
								entity.setDead();
								counter++;
								tempCount++;
							}
						}
						if (mobType.equalsIgnoreCase("villager") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityVillager)
							{
								entity.setDead();
								counter++;
								tempCount++;
							}
						}
						if (mobType.equalsIgnoreCase("golem") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityGolem)
							{
								entity.setDead();
								counter++;
								tempCount++;
							}
						}
						if (mobType.equalsIgnoreCase("tamed") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
							{
								entity.setDead();
								counter++;
								tempCount++;
							}
						}
						if (mobType.equalsIgnoreCase("boss") || mobType.equalsIgnoreCase("all"))
						{
							if (entity instanceof EntityDragon)
							{
								for (EntityDragonPart part : ((EntityDragon) entity).dragonPartArray)
								{
									part.setDead();
								}
								((EntityLiving) entity).setDead();
								counter++;
								tempCount++;
							}
							else if (entity instanceof EntityWither)
							{
								((EntityLiving) entity).setDead();
								counter++;
								tempCount++;
							}
						}
						if (tempCount == 29)
						{
							tempCount = 0;
							var9temp = var9;
							var10temp = var10;
							return;
						}
					}
				}
			}
		}
		isComplete = true;
	}

	@Override
	public void onComplete()
	{
		if (playerCommand)
		{
			OutputHandler.chatConfirmation(player, Localization.format(Localization.BUTCHERED, counter));
		}
		else
		{
			sender.sendChatToPlayer(Localization.format(Localization.BUTCHERED, counter));
		}
	}

	@Override
	public boolean isComplete()
	{
		return isComplete;
	}

	@Override
	public boolean editsBlocks()
	{
		return false;
	}

}
