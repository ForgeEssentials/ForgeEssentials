package com.forgeessentials.protection;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.PermissionEvent;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.protection.commands.CommandItemPermission;
import com.forgeessentials.protection.commands.CommandProtectionDebug;
import com.forgeessentials.protection.commands.CommandUpgradePermissions;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;

@FEModule(name = "Protection", parentMod = ForgeEssentials.class, isCore = true, canDisable = false)
public class ModuleProtection
{

    public final static String BASE_PERM = "fe.protection";

    public final static String PERM_PVP = BASE_PERM + ".pvp";

    public final static String PERM_GAMEMODE = BASE_PERM + ".gamemode";
    public final static String PERM_INVENTORY_GROUP = BASE_PERM + ".inventorygroup";

    public final static String PERM_USE = BASE_PERM + ".use";
    public final static String PERM_BREAK = BASE_PERM + ".break";
    public final static String PERM_PLACE = BASE_PERM + ".place";
    public final static String PERM_INTERACT = BASE_PERM + ".interact";
    public final static String PERM_INTERACT_ENTITY = BASE_PERM + ".interact.entity";
    public final static String PERM_DAMAGE_TO = BASE_PERM + ".damageto";
    public final static String PERM_DAMAGE_BY = BASE_PERM + ".damageby";
    public final static String PERM_INVENTORY = BASE_PERM + ".inventory";
    public final static String PERM_EXIST = BASE_PERM + ".exist";
    public final static String PERM_EXPLOSION = BASE_PERM + ".explosion";

    public final static String PERM_MOBSPAWN = BASE_PERM + ".mobspawn";
    public final static String PERM_MOBSPAWN_NATURAL = PERM_MOBSPAWN + ".natural";
    public final static String PERM_MOBSPAWN_FORCED = PERM_MOBSPAWN + ".forced";

    public static final String ZONE = BASE_PERM + ".zone";
    public static final String ZONE_KNOCKBACK = ZONE + ".knockback";
    public static final String ZONE_DAMAGE = ZONE + ".damage";
    public static final String ZONE_DAMAGE_INTERVAL = ZONE_DAMAGE + ".interval";
    public static final String ZONE_COMMAND = ZONE + ".command";
    public static final String ZONE_COMMAND_INTERVAL = ZONE_COMMAND + ".interval";
    public static final String ZONE_POTION = ZONE + ".potion";
    public static final String ZONE_POTION_INTERVAL = ZONE_POTION + ".interval";

    public static final String MSG_ZONE_DENIED = "You are not allowed to enter this area!";

    private static final Class<?>[] damageEntityClasses = new Class<?>[] {
            // EntityAgeable
            EntityVillager.class,
            // EntityAnimal
            EntityChicken.class, EntityCow.class, EntityMooshroom.class, EntityHorse.class, EntityPig.class,
            // EntityTameable
            EntityOcelot.class, EntityWolf.class,
            // EntityMob
            EntityBlaze.class, EntityCreeper.class, EntityEnderman.class, EntityGiantZombie.class, EntitySilverfish.class, EntitySkeleton.class,
            EntitySpider.class, EntityWitch.class, EntityWither.class, EntityZombie.class, EntityPigZombie.class,
            // EntityGolem
            EntityIronGolem.class, EntitySnowman.class,
            // EntityWaterMob
            EntitySquid.class,
    /* -- end of list -- */
    };

    private static final DamageSource[] damageByTypes = new DamageSource[] { DamageSource.anvil, DamageSource.cactus, DamageSource.drown, DamageSource.fall,
            DamageSource.fallingBlock, DamageSource.generic, DamageSource.inFire, DamageSource.inWall, DamageSource.lava, DamageSource.magic,
            DamageSource.onFire, DamageSource.outOfWorld, DamageSource.starve, DamageSource.wither };

    public static Set<UUID> debugModePlayers = new HashSet<>();

    /* ------------------------------------------------------------ */

    @SuppressWarnings("unused")
    private ProtectionEventHandler protectionHandler;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        protectionHandler = new ProtectionEventHandler();

        FECommandManager.registerCommand(new CommandItemPermission());
        FECommandManager.registerCommand(new CommandProtectionDebug());
        FECommandManager.registerCommand(new CommandUpgradePermissions());
    }

    public static String getItemName(Item item)
    {
        try
        {
            return item.getItemStackDisplayName(new ItemStack(item));
        }
        catch (Exception | NoClassDefFoundError e)
        {
            return item.getUnlocalizedName();
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void registerPermissions(FEModuleServerInitEvent event)
    {
        // ----------------------------------------
        // Other
        APIRegistry.perms.registerPermission(PERM_PVP, RegisteredPermValue.TRUE, "If denied for at least one of two fighting players, PvP will be disabled");
        APIRegistry.perms.registerPermissionProperty(PERM_GAMEMODE, "-1", "Force gamemode (-1 = none / default, 0 = survival, 1 = creative, 2 = adventure)");
        APIRegistry.perms.registerPermissionProperty(PERM_INVENTORY_GROUP, "default",
                "Inventory group property - can be set to any identifier to separate inventories for certain regions");
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, RegisteredPermValue.TRUE, "Allow interacting with entities (villagers, dogs, horses)");
        APIRegistry.perms.registerPermission(PERM_EXPLOSION, RegisteredPermValue.TRUE, "(global) Allows explosions.");

        // ----------------------------------------
        // Damage
        APIRegistry.perms.registerPermission(PERM_DAMAGE_TO, RegisteredPermValue.TRUE, "Allow damaging entities");
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY, RegisteredPermValue.TRUE, "Allow getting hurt by entities");
        for (Class<?> entityClass : damageEntityClasses)
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_TO + "." + entityClass.getSimpleName(), RegisteredPermValue.TRUE);
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + entityClass.getSimpleName(), RegisteredPermValue.TRUE);
        }
        for (DamageSource dmgType : damageByTypes)
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + dmgType.getDamageType(), RegisteredPermValue.TRUE);
        }

        // ----------------------------------------
        // Register mobs
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN + Zone.PERMISSION_ASTERIX, RegisteredPermValue.TRUE, "(global) Allow spawning of mobs");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + Zone.ALL_PERMS, RegisteredPermValue.TRUE,
                "(global) Allow natural spawning of mobs (random spawn)");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + Zone.ALL_PERMS, RegisteredPermValue.TRUE,
                "(global) Allow forced spawning of mobs (mob-spawners)");

        for (Entry<String, Class<? extends Entity>> e : ((Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping).entrySet())
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + e.getKey(), RegisteredPermValue.TRUE);
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + e.getKey(), RegisteredPermValue.TRUE);
            }
        for (MobType mobType : MobType.values())
        {
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(false), RegisteredPermValue.TRUE);
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(true), RegisteredPermValue.TRUE);
            APIRegistry.perms.registerPermission(mobType.getDamageByPermission(), RegisteredPermValue.TRUE);
            APIRegistry.perms.registerPermission(mobType.getDamageToPermission(), RegisteredPermValue.TRUE);
        }

        // ----------------------------------------
        // Register items
        APIRegistry.perms.registerPermission(PERM_USE + Zone.ALL_PERMS, RegisteredPermValue.TRUE, "Allow using items");
        APIRegistry.perms.registerPermission(PERM_INVENTORY + Zone.ALL_PERMS, RegisteredPermValue.TRUE,
                "Allow having item in inventory. Item will be dropped if not allowed.");
        APIRegistry.perms.registerPermission(PERM_EXIST + Zone.ALL_PERMS, RegisteredPermValue.TRUE,
                "Allow having item in inventory. Item will be destroyed if not allowed.");
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
            if (!(item instanceof ItemBlock))
            {
                String itemPerm = "." + getItemId(item) + Zone.ALL_PERMS;
                APIRegistry.perms.registerPermission(PERM_USE + itemPerm, RegisteredPermValue.TRUE, "USE " + getItemName(item));
                APIRegistry.perms.registerPermission(PERM_INVENTORY + itemPerm, RegisteredPermValue.TRUE, "INVENTORY " + getItemName(item));
                APIRegistry.perms.registerPermission(PERM_INVENTORY + itemPerm, RegisteredPermValue.TRUE, "EXIST " + getItemName(item));
            }

        // ----------------------------------------
        // Register blocks
        APIRegistry.perms.registerPermission(PERM_BREAK + Zone.ALL_PERMS, RegisteredPermValue.TRUE, "Allow breaking blocks");
        APIRegistry.perms.registerPermission(PERM_PLACE + Zone.ALL_PERMS, RegisteredPermValue.TRUE, "Allow placing blocks");
        APIRegistry.perms.registerPermission(PERM_INTERACT + Zone.ALL_PERMS, RegisteredPermValue.TRUE,
                "Allow interacting with blocks (button, chest, workbench)");
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            String blockPerm = "." + getBlockId(block) + Zone.ALL_PERMS;
            APIRegistry.perms.registerPermission(PERM_BREAK + blockPerm, RegisteredPermValue.TRUE, "BREAK " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_PLACE + blockPerm, RegisteredPermValue.TRUE, "PLACE " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_INTERACT + blockPerm, RegisteredPermValue.TRUE, "INTERACT " + block.getLocalizedName());
        }

        // ----------------------------------------
        // Register zone permissions
        APIRegistry.perms.registerPermissionDescription(ZONE, "Worldborder permissions");
        APIRegistry.perms.registerPermission(ZONE_KNOCKBACK, RegisteredPermValue.FALSE, "Deny players from entering this area");
        APIRegistry.perms.registerPermissionProperty(ZONE_DAMAGE, null, "Apply this amount of damage to players, if they are in this area");
        APIRegistry.perms.registerPermissionProperty(ZONE_DAMAGE_INTERVAL, "1000",
                "Time interval in milliseconds for applying damage-effect. Zero = once only.");
        APIRegistry.perms.registerPermissionProperty(ZONE_COMMAND, null, "Execute this command if a player enters the area");
        APIRegistry.perms.registerPermissionProperty(ZONE_COMMAND_INTERVAL, "0", "Time interval in milliseconds for executing command. Zero = once only.");
        APIRegistry.perms
                .registerPermissionProperty(
                        ZONE_POTION,
                        null,
                        "Apply potion effects to players who enter this area. Comma separated list of \"ID:duration:amplifier\" pairs. See http://www.minecraftwiki.net/wiki/Potion_effects#Parameters");
        APIRegistry.perms.registerPermissionProperty(ZONE_POTION_INTERVAL, "2000",
                "Time interval in milliseconds for applying potion-effects. Zero = once only.");
    }

    @SubscribeEvent
    public void afterPermissionLoadEvent(PermissionEvent.AfterLoad event)
    {
        if (event.serverZone.checkGroupPermission(Zone.GROUP_DEFAULT, "fe.internal.newprotectionpermissions") == null)
        {
            CommandUpgradePermissions.upgradePermissions(event.serverZone);
            event.serverZone.setGroupPermission(Zone.GROUP_DEFAULT, "fe.internal.newprotectionpermissions", true);
        }
    }

    /* ------------------------------------------------------------ */

    public static void setDebugMode(EntityPlayer player, boolean value)
    {
        if (value)
            debugModePlayers.add(player.getPersistentID());
        else
            debugModePlayers.remove(player.getPersistentID());
    }

    public static boolean isDebugMode(EntityPlayer player)
    {
        return debugModePlayers.contains(player.getPersistentID());
    }

    /* ------------------------------------------------------------ */

    public static String getBlockId(Block block)
    {
        return GameData.getBlockRegistry().getNameForObject(block).replace(':', '.');
    }

    public static String getBlockPermission(Block block, int meta)
    {
        if (meta == 0 || meta == 32767)
            return getBlockId(block);
        else
            return getBlockId(block) + "." + meta;
    }

    public static String getBlockPermission(Block block, World world, int x, int y, int z)
    {
        return getBlockPermission(block, block.getDamageValue(world, x, y, z));
    }

    public static String getBlockBreakPermission(Block block, World world, int x, int y, int z)
    {
        return ModuleProtection.PERM_BREAK + "." + getBlockPermission(block, world, x, y, z);
    }

    public static String getBlockPlacePermission(Block block, World world, int x, int y, int z)
    {
        return ModuleProtection.PERM_PLACE + "." + getBlockPermission(block, world, x, y, z);
    }

    public static String getBlockInteractPermission(Block block, World world, int x, int y, int z)
    {
        return ModuleProtection.PERM_INTERACT + "." + getBlockPermission(block, world, x, y, z);
    }

    public static String getBlockBreakPermission(Block block, int meta)
    {
        return ModuleProtection.PERM_BREAK + "." + getBlockPermission(block, meta);
    }

    public static String getBlockPlacePermission(Block block, int meta)
    {
        return ModuleProtection.PERM_PLACE + "." + getBlockPermission(block, meta);
    }

    public static String getBlockInteractPermission(Block block, int meta)
    {
        return ModuleProtection.PERM_INTERACT + "." + getBlockPermission(block, meta);
    }

    /* ------------------------------------------------------------ */

    public static String getItemId(Item item)
    {
        return GameData.getItemRegistry().getNameForObject(item).replace(':', '.');
    }

    public static String getItemPermission(ItemStack stack, boolean checkMeta)
    {
        int dmg = stack.getItemDamage();
        if (!checkMeta || dmg == 0 || dmg == 32767)
            return getItemId(stack.getItem());
        else
            return getItemId(stack.getItem()) + "." + dmg;
    }

    public static String getItemPermission(ItemStack stack)
    {
        return getItemPermission(stack, true);
    }

    public static String getItemUsePermission(ItemStack stack)
    {
        return ModuleProtection.PERM_USE + "." + getItemPermission(stack);
    }

    public static String getItemBanPermission(ItemStack stack)
    {
        return ModuleProtection.PERM_EXIST + "." + getItemPermission(stack);
    }

    public static String getItemInventoryPermission(ItemStack stack)
    {
        return ModuleProtection.PERM_INVENTORY + "." + getItemPermission(stack);
    }

}
