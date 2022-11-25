package com.forgeessentials.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.passive.AmbientEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.GolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TameableEntity;

public enum MobType
{
    BOSS, GOLEM, HOSTILE, PASSIVE, TAMED, TAMABLE, VILLAGER, UNKNOWN;

    public String getDamageToPermission()
    {
        return ModuleProtection.PERM_DAMAGE_TO + "." + toString().toLowerCase();
    }

    public String getDamageByPermission()
    {
        return ModuleProtection.PERM_DAMAGE_BY + "." + toString().toLowerCase();
    }

    public String getSpawnPermission(boolean forced)
    {
        return (forced ? ModuleProtection.PERM_MOBSPAWN_FORCED : ModuleProtection.PERM_MOBSPAWN_NATURAL) + ".type." + toString().toLowerCase();
    }

    public static MobType getMobType(Entity entity)
    {
        if (entity instanceof EnderDragonEntity || entity instanceof WitherEntity)
            return MobType.BOSS;

        if (entity instanceof GolemEntity)
            return MobType.GOLEM;

        if (entity instanceof SlimeEntity)
            return ((SlimeEntity) entity).getSize() >= 2 ? MobType.HOSTILE : MobType.PASSIVE;

        if (entity instanceof TameableEntity)
            return ((TameableEntity) entity).isTame() ? MobType.TAMED : MobType.TAMABLE;

        // Check for other creatures
        if (entity instanceof AnimalEntity || entity instanceof AmbientEntity || entity instanceof SquidEntity)
            return MobType.PASSIVE;

        if (entity instanceof VillagerEntity)
            return MobType.VILLAGER;

        if (entity instanceof MobEntity || entity instanceof GhastEntity)
            return MobType.HOSTILE;

        return MobType.UNKNOWN;
    }

}
