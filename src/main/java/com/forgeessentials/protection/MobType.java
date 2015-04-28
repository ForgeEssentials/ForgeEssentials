package com.forgeessentials.protection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.EntityDragon;
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
        if (entity instanceof EntityDragon || entity instanceof EntityWither)
            return MobType.BOSS;

        if (entity instanceof EntityGolem)
            return MobType.GOLEM;

        if (entity instanceof EntitySlime)
            return ((EntitySlime) entity).getSlimeSize() >= 2 ? MobType.HOSTILE : MobType.PASSIVE;

        if (entity instanceof EntityTameable)
            return ((EntityTameable) entity).isTamed() ? MobType.TAMED : MobType.TAMABLE;

        // Check for other creatures
        if (entity instanceof EntityAnimal || entity instanceof EntityAmbientCreature || entity instanceof EntitySquid)
            return MobType.PASSIVE;

        if (entity instanceof EntityVillager)
            return MobType.VILLAGER;

        if (entity instanceof EntityMob || entity instanceof EntityGhast)
            return MobType.HOSTILE;

        return MobType.UNKNOWN;
    }

}
