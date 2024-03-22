package com.forgeessentials.protection;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.TamableAnimal;

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
        return (forced ? ModuleProtection.PERM_MOBSPAWN_FORCED : ModuleProtection.PERM_MOBSPAWN_NATURAL) + ".type."
                + toString().toLowerCase();
    }

    public static MobType getMobType(Entity entity)
    {
        if (entity instanceof EnderDragon || entity instanceof WitherBoss)
            return MobType.BOSS;

        if (entity instanceof AbstractGolem)
            return MobType.GOLEM;

        if (entity instanceof Slime)
            return ((Slime) entity).getSize() >= 2 ? MobType.HOSTILE : MobType.PASSIVE;

        if (entity instanceof TamableAnimal)
            return ((TamableAnimal) entity).isTame() ? MobType.TAMED : MobType.TAMABLE;

        // Check for other creatures
        if (entity instanceof Animal || entity instanceof AmbientCreature || entity instanceof Squid)
            return MobType.PASSIVE;

        if (entity instanceof Villager)
            return MobType.VILLAGER;

        if (entity instanceof Mob || entity instanceof Ghast)
            return MobType.HOSTILE;

        return MobType.UNKNOWN;
    }

}
