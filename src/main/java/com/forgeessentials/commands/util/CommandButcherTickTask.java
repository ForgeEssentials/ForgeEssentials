package com.forgeessentials.commands.util;

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
import net.minecraftforge.common.DimensionManager;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.tasks.ITickTask;

public class CommandButcherTickTask implements ITickTask {
    double MAX_ENTITY_RADIUS;
    boolean isComplete;
    AxisAlignedBB aabb;
    EntityPlayer player;
    ICommandSender sender;
    boolean playerCommand;
    String mobType;
    int counter, radius, tempCount;
    int var4, var5, var6, var7;
    int var9temp, var10temp;
    World world;

    public CommandButcherTickTask(EntityPlayer sender, String mobType, AxisAlignedBB aabb, int radius)
    {
        player = sender;
        this.sender = sender;
        this.mobType = mobType;
        this.radius = radius;
        if (radius > -1)
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
        if (radius >= -1)
        {
            this.aabb = aabb;
            var4 = var9temp = MathHelper.floor_double((aabb.minX - MAX_ENTITY_RADIUS) / 16.0D);
            var5 = MathHelper.floor_double((aabb.maxX + MAX_ENTITY_RADIUS) / 16.0D);
            var6 = var10temp = MathHelper.floor_double((aabb.minZ - MAX_ENTITY_RADIUS) / 16.0D);
            var7 = MathHelper.floor_double((aabb.maxZ + MAX_ENTITY_RADIUS) / 16.0D);
        }
        world = DimensionManager.getWorld(worldID);
        counter = 0;
        playerCommand = false;
        tempCount = 0;
    }

    @Override
    public void tick()
    {
        Set<String> typeSet;
        if (radius == -1)
        {
            for (Object entity : world.loadedEntityList)
            {
                if (entity instanceof EntityLiving && !(entity instanceof EntityPlayer))
                {
                    if (mobType.equalsIgnoreCase("boss") || mobType.equalsIgnoreCase("all"))
                    {
                        typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS);
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
                    else if (shouldKill((EntityLiving) entity))
                    {
                        ((EntityLiving) entity).setDead();
                        counter++;
                        tempCount++;
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
                        if (mobType.equalsIgnoreCase("boss") || mobType.equalsIgnoreCase("all"))
                        {
                            typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS);
                            if (entity instanceof EntityDragon)
                            {
                                for (EntityDragonPart part : ((EntityDragon) entity).dragonPartArray)
                                {
                                    part.setDead();
                                }
                                entity.setDead();
                                counter++;
                                tempCount++;
                            }
                            else if (entity instanceof EntityWither)
                            {
                                entity.setDead();
                                counter++;
                                tempCount++;
                            }
                            else if (typeSet.contains(entity.getClass().getName()))
                            {
                                entity.setDead();
                                counter++;
                                tempCount++;
                            }
                        }
                        else if (shouldKill(entity))
                        {
                            entity.setDead();
                            counter++;
                            tempCount++;
                        }
                        if (tempCount == 29)
                        {
                            tempCount = 0;
                            return;
                        }
                    }
                }
            }
        }
        isComplete = true;
    }

    private boolean shouldKill(EntityLiving entity)
    {
        Set<String> typeSet, tameableSet;

        if (mobType.equalsIgnoreCase("all"))
        {
            return true;
        }
        else if (mobType.equalsIgnoreCase("hostile"))
        {
            typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.HOSTILE);
            if (entity instanceof EntityMob || entity instanceof EntitySlime || entity instanceof EntityGhast)
            {
                return true;
            }
            else if (typeSet.contains(entity.getClass().getName()))
            {
                return true;
            }
        }
        else if (mobType.equalsIgnoreCase("passive"))
        {
            typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.PASSIVE);
            tameableSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE);
            if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid)
            {
                if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else if (tameableSet.contains(entity.getClass().getName()))
            {
                if (MobTypeRegistry.isTamed(entity))
                {
                    return false;
                }
                else
                {
                    return true;
                }
            }
            else if (typeSet.contains(entity.getClass().getName()))
            {
                return true;
            }
        }
        else if (mobType.equalsIgnoreCase("villager"))
        {
            typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.VILLAGER);
            if (entity instanceof EntityVillager)
            {
                return true;
            }
            else if (typeSet.contains(entity.getClass().getName()))
            {
                return true;
            }
        }
        else if (mobType.equalsIgnoreCase("golem"))
        {
            typeSet = MobTypeRegistry.getCollectionForMobType(EnumMobType.GOLEM);
            if (entity instanceof EntityGolem)
            {
                return true;
            }
            else if (typeSet.contains(entity.getClass().getName()))
            {
                return true;
            }
        }
        else if (mobType.equalsIgnoreCase("tamed"))
        {
            if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
            {
                return true;
            }
            if (MobTypeRegistry.isTamed(entity))
            {
                return true;
            }
        }

        return false;
    }

    @Override
    public void onComplete()
    {
        OutputHandler.chatConfirmation(player, String.format("%s mobs killed.", counter));
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
