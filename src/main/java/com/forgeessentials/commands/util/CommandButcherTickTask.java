package com.forgeessentials.commands.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.EnumMobType;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.misc.TaskRegistry.TickTask;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandButcherTickTask implements TickTask
{

    public static enum ButcherMobType
    {
        ALL, HOSTILE, PASSIVE, VILLAGER, TAMABLE, TAMED, GOLEM, BOSS;

        public static List<String> getNames()
        {
            List<String> result = new ArrayList<>();
            for (ButcherMobType type : ButcherMobType.values())
                result.add(type.name());
            return result;
        }
    }

    private ICommandSender sender;
    private ButcherMobType mobType;
    private AxisAlignedBB aabb;
    private World world;
    private int radius;

    private int maxChunkX;
    private int maxChunkZ;
    private int minChunkX;
    private int minChunkZ;
    private int killCount;
    private int tickKillCount;

    private static final int MAX_TICK_KILLS = 1;

    public CommandButcherTickTask(ICommandSender sender, World world, ButcherMobType mobType, AxisAlignedBB aabb, int radius)
    {
        this.sender = sender;
        this.mobType = mobType;
        this.radius = radius;
        this.world = world;
        if (radius > -1)
        {
            this.aabb = aabb;
            minChunkX = MathHelper.floor_double((aabb.minX - World.MAX_ENTITY_RADIUS) / 16.0D);
            maxChunkX = MathHelper.floor_double((aabb.maxX + World.MAX_ENTITY_RADIUS) / 16.0D);
            minChunkZ = MathHelper.floor_double((aabb.minZ - World.MAX_ENTITY_RADIUS) / 16.0D);
            maxChunkZ = MathHelper.floor_double((aabb.maxZ + World.MAX_ENTITY_RADIUS) / 16.0D);
        }
    }

    public CommandButcherTickTask(ICommandSender sender, World world, String mobType, AxisAlignedBB aabb, int radius)
    {
        this(sender, world, CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase()), aabb, radius);
    }

    public static void schedule(ICommandSender sender, World world, String mobType, AxisAlignedBB aabb, int radius) throws CommandException
    {
        try
        {
            ButcherMobType mobT = CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase());
            TaskRegistry.schedule(new CommandButcherTickTask(sender, world, mobT, aabb, radius));
        }
        catch (IllegalArgumentException e)
        {
            throw new TranslatedCommandException("Unknown mob type. Mob types are " + StringUtils.join(CommandButcherTickTask.ButcherMobType.values(), ", "));
        }
    }

    @Override
    public boolean tick()
    {
        tickKillCount = 0;
        if (radius < -1)
            return true;
        else if (radius == -1)
        {
            for (Object entity : world.loadedEntityList)
                if (entity instanceof EntityLiving)
                {
                    checkEntity((EntityLiving) entity);
                    if (tickKillCount >= 32)
                        return false;
                }
            ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
        }
        else
        {
            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX)
                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ)
                    if (world.getChunkProvider().chunkExists(chunkX, chunkZ))
                    {
                        List<EntityLiving> list = new LinkedList<EntityLiving>();
                        world.getChunkFromChunkCoords(chunkX, chunkZ).getEntitiesOfTypeWithinAAAB(EntityLiving.class, aabb, list, null);
                        for (EntityLiving entity : list)
                        {
                            checkEntity(entity);
                            if (tickKillCount >= MAX_TICK_KILLS)
                                return false;
                        }
                    }
            ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
        }
        return true;
    }

    private void checkEntity(EntityLiving entity)
    {
        if (shouldKill(entity))
        {
            killEntity(entity);
            killCount++;
            tickKillCount++;
        }
    }

    private boolean shouldKill(EntityLiving entity)
    {
        String className = entity.getClass().getName();
        switch (mobType)
        {
        case ALL:
            return true;
        case HOSTILE:
            if (entity instanceof EntityMob || entity instanceof EntityGhast)
                return true;
            if (entity instanceof EntitySlime && ((EntitySlime) entity).getSlimeSize() > 0)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.HOSTILE).contains(className))
                return true;
            return false;
        case PASSIVE:
            // Filter out tamed creatures
            if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
                return false;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE).contains(className) && MobTypeRegistry.isTamed(entity))
                return false;
            // Check for other creatures
            if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.PASSIVE).contains(className))
                return true;
            return false;
        case VILLAGER:
            if (entity instanceof EntityVillager)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.VILLAGER).contains(className))
                return true;
            return false;
        case TAMABLE:
            if (entity instanceof EntityTameable)
                return true;
            return false;
        case TAMED:
            if (entity instanceof EntityTameable && ((EntityTameable) entity).isTamed())
                return true;
            return false;
        case GOLEM:
            if (entity instanceof EntityGolem)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.GOLEM).contains(className))
                return true;
            return false;
        case BOSS:
            if (entity instanceof EntityDragon || entity instanceof EntityWither)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS).contains(className))
                return true;
        default:
            return false;
        }
    }

    private static void killEntity(Entity entity)
    {
        if (entity instanceof EntityDragon)
        {
            for (EntityDragonPart part : ((EntityDragon) entity).dragonPartArray)
                part.setDead();
        }
        entity.setDead();
    }

    @Override
    public boolean editsBlocks()
    {
        return false;
    }

}
