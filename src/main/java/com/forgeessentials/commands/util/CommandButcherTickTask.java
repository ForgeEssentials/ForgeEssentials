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

import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.EnderDragonPart;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.EntityGetter;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.server.level.ServerLevel;

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

    private CommandSourceStack sender;
    private ButcherMobType mobType;
    private AABB aabb;
    private ServerLevel world;
    private int radius;

    private int maxChunkX;
    private int maxChunkZ;
    private int minChunkX;
    private int minChunkZ;
    private int killCount;
    private int tickKillCount;

    private static final int MAX_TICK_KILLS = 1;

    public CommandButcherTickTask(CommandSourceStack sender, ServerLevel world, ButcherMobType mobType, AABB aabb,
            int radius)
    {
        this.sender = sender;
        this.mobType = mobType;
        this.radius = radius;
        this.world = world;
        if (radius > -1)
        {
            this.aabb = aabb;
            minChunkX = Mth.floor((aabb.minX - world.getMaxEntityRadius()) / 16.0D);
            maxChunkX = Mth.floor((aabb.maxX + world.getMaxEntityRadius()) / 16.0D);
            minChunkZ = Mth.floor((aabb.minZ - world.getMaxEntityRadius()) / 16.0D);
            maxChunkZ = Mth.floor((aabb.maxZ + world.getMaxEntityRadius()) / 16.0D);
        }
    }

    public CommandButcherTickTask(CommandSourceStack sender, ServerLevel world, String mobType, AABB aabb,
            int radius)
    {
        this(sender, world, CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase()), aabb, radius);
    }

    public static void schedule(CommandSourceStack sender, ServerLevel world, String mobType, AABB aabb, int radius)
            throws CommandRuntimeException
    {
        try
        {
            ButcherMobType mobT = CommandButcherTickTask.ButcherMobType.valueOf(mobType.toUpperCase());
            TaskRegistry.schedule(new CommandButcherTickTask(sender, world, mobT, aabb, radius));
        }
        catch (IllegalArgumentException e)
        {
            ChatOutputHandler.chatError(sender, "Unknown mob type. Mob types are "
                    + StringUtils.join(CommandButcherTickTask.ButcherMobType.values(), ", "));
            return;
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
            for (Object entity : world.getAllEntities())
                if (entity instanceof LivingEntity)
                {
                    checkEntity((LivingEntity) entity);
                    if (tickKillCount >= 32)
                        return false;
                }
            ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
        }
        else
        {
//            for (int chunkX = minChunkX; chunkX <= maxChunkX; ++chunkX)
//                for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; ++chunkZ)
//                    if (world.getChunk(chunkX, chunkZ) != null)
//                    {
            List<LivingEntity> list = world.getEntitiesOfClass(LivingEntity.class, aabb);
            for (LivingEntity entity : list)
            {
                checkEntity(entity);
                if (tickKillCount >= MAX_TICK_KILLS)
                    return false;
            }
//                    }
            ChatOutputHandler.chatConfirmation(sender, Translator.format("%s mobs killed.", killCount));
        }
        return true;
    }

    private void checkEntity(LivingEntity entity)
    {
        if (shouldKill(entity))
        {
            killEntity(entity);
            killCount++;
            tickKillCount++;
        }
    }

    private boolean shouldKill(LivingEntity entity)
    {
        String className = entity.getClass().getName();
        switch (mobType)
        {
        case ALL:
            return true;
        case HOSTILE:
            if (entity instanceof Mob || entity instanceof Ghast)
                return true;
            if (entity instanceof Slime && ((Slime) entity).getSize() > 0)
                return true;
            return MobTypeRegistry.getCollectionForMobType(EnumMobType.HOSTILE).contains(className);
            case PASSIVE:
            // Filter out tamed creatures
            if (entity instanceof TamableAnimal && ((TamableAnimal) entity).isTame())
                return false;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.TAMEABLE).contains(className)
                    && MobTypeRegistry.isTamed(entity))
                return false;
            // Check for other creatures
            if (entity instanceof Animal || entity instanceof AmbientCreature || entity instanceof Squid)
                return true;
                return MobTypeRegistry.getCollectionForMobType(EnumMobType.PASSIVE).contains(className);
            case VILLAGER:
            if (entity instanceof Villager)
                return true;
                return MobTypeRegistry.getCollectionForMobType(EnumMobType.VILLAGER).contains(className);
            case TAMABLE:
                return entity instanceof TamableAnimal;
            case TAMED:
                return entity instanceof TamableAnimal && ((TamableAnimal) entity).isTame();
            case GOLEM:
            if (entity instanceof AbstractGolem)
                return true;
                return MobTypeRegistry.getCollectionForMobType(EnumMobType.GOLEM).contains(className);
            case BOSS:
            if (entity instanceof EnderDragon || entity instanceof WitherBoss)
                return true;
            if (MobTypeRegistry.getCollectionForMobType(EnumMobType.BOSS).contains(className))
                return true;
        default:
            return false;
        }
    }

    private static void killEntity(Entity entity)
    {
        if (entity instanceof EnderDragon)
        {
            for (EnderDragonPart part : ((EnderDragon) entity).getSubEntities())
                part.kill();;
        }
        entity.kill();
    }

    @Override
    public boolean editsBlocks()
    {
        return false;
    }

}
