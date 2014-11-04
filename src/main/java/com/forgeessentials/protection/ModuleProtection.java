package com.forgeessentials.protection;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
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
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.protection.commands.CommandItemPermission;
import com.forgeessentials.protection.commands.CommandProtectionDebug;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameData;

@FEModule(name = "protection", parentMod = ForgeEssentials.class, isCore = true)
public class ModuleProtection {

    public final static String BASE_PERM = "fe.protection";

    public final static String PERM_PVP = BASE_PERM + ".pvp";

    public final static String PERM_GAMEMODE = BASE_PERM + ".gamemode";

    public final static String PERM_USE = BASE_PERM + ".use";
    public final static String PERM_BREAK = BASE_PERM + ".break";
    public final static String PERM_PLACE = BASE_PERM + ".place";
    public final static String PERM_INTERACT = BASE_PERM + ".interact";
    public final static String PERM_INTERACT_ENTITY = BASE_PERM + ".interact.entity";
    public final static String PERM_DAMAGE_TO = BASE_PERM + ".damageto";
    public final static String PERM_DAMAGE_BY = BASE_PERM + ".damageby";
    public final static String PERM_INVENTORY = BASE_PERM + ".inventory";

    private final static String PERM_OVERRIDE = BASE_PERM + ".override";
    public final static String PERM_OVERRIDE_USE = PERM_OVERRIDE + ".use";
    public final static String PERM_OVERRIDE_BREAK = PERM_OVERRIDE + ".break";
    public final static String PERM_OVERRIDE_PLACE = PERM_OVERRIDE + ".place";
    public final static String PERM_OVERRIDE_INTERACT = PERM_OVERRIDE + ".interact";
    public final static String PERM_OVERRIDE_INTERACT_ENTITY = PERM_OVERRIDE + ".interact.entity";

    public final static String PERM_MOBSPAWN = BASE_PERM + ".mobspawn";
    public final static String PERM_MOBSPAWN_NATURAL = PERM_MOBSPAWN + ".natural";
    public final static String PERM_MOBSPAWN_FORCED = PERM_MOBSPAWN + ".forced";

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

    public static Set<String> debugModePlayers = new HashSet<>();

    @SuppressWarnings("unused")
    private ProtectionEventHandler protectionHandler;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        protectionHandler = new ProtectionEventHandler();
    }

    public static String getItemName(Item item)
    {
        try
        {
            return item.getItemStackDisplayName(new ItemStack(item));
        }
        catch (Exception e)
        {
            return item.getUnlocalizedName();
        }
    }

    @SuppressWarnings("unchecked")
    @SubscribeEvent
    public void registerPermissions(FEModuleServerInitEvent ev)
    {
        FunctionHelper.registerServerCommand(new CommandItemPermission());
        FunctionHelper.registerServerCommand(new CommandProtectionDebug());
        FunctionHelper.registerServerCommand(new ProtectCommand());

        // ----------------------------------------
        // Other
        APIRegistry.perms.registerPermission(PERM_PVP, RegisteredPermValue.TRUE, "Allow PvP");
        APIRegistry.perms.registerPermissionProperty(PERM_GAMEMODE, "-1", "Force gamemode (-1 = none, 0 = survival, 1 = creative, 2 = adventure)");
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, RegisteredPermValue.TRUE, "Allow interacting with entities (villagers, dogs, horses)");

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
        // Overrides
        APIRegistry.perms.registerPermission(PERM_OVERRIDE + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.OP, "Override (ignore) protection permissions");
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_USE, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_BREAK, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_PLACE, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_INTERACT, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_INTERACT_ENTITY, RegisteredPermValue.OP);

        // ----------------------------------------
        // Register mobs
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE, "Allow spawning of mobs");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow natural spawning of mobs (random spawn in the dark)");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow forced spawning of mobs (spawners, spawn-eggs)");
        for (Entry<String, Class<?>> e : (Set<Entry<String, Class<?>>>) EntityList.stringToClassMapping.entrySet())
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + e.getKey(), RegisteredPermValue.TRUE);
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + e.getKey(), RegisteredPermValue.TRUE);
            }

        // ----------------------------------------
        // Register items
        APIRegistry.perms.registerPermission(PERM_USE + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow using items");
        APIRegistry.perms.registerPermission(PERM_INVENTORY + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow having item in inventory. Item will be dropped if not allowed.");
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
            if (!(item instanceof ItemBlock))
            {
                String itemPerm = "." + item.getUnlocalizedName() + IPermissionsHelper.ALL_PERMS;
                APIRegistry.perms.registerPermission(PERM_USE + itemPerm, RegisteredPermValue.TRUE, "USE " + getItemName(item));
                APIRegistry.perms.registerPermission(PERM_INVENTORY + itemPerm, RegisteredPermValue.TRUE, "INVENTORY " + getItemName(item));
            }

        // ----------------------------------------
        // Register blocks
        APIRegistry.perms.registerPermission(PERM_BREAK + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow breaking blocks");
        APIRegistry.perms.registerPermission(PERM_PLACE + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow placing blocks");
        APIRegistry.perms.registerPermission(PERM_INTERACT + IPermissionsHelper.ALL_PERMS, RegisteredPermValue.TRUE, "Allow interacting with blocks (button, chest, workbench)");
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            String blockPerm = "." + block.getUnlocalizedName() + IPermissionsHelper.ALL_PERMS;
            APIRegistry.perms.registerPermission(PERM_BREAK + blockPerm, RegisteredPermValue.TRUE, "BREAK " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_PLACE + blockPerm, RegisteredPermValue.TRUE, "PLACE " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_INTERACT + blockPerm, RegisteredPermValue.TRUE, "INTERACT " + block.getLocalizedName());
        }
    }

    public static void enableDebugMode(EntityPlayer player)
    {
        debugModePlayers.add(player.getCommandSenderName());
    }

    public static void disableDebugMode(EntityPlayer player)
    {
        debugModePlayers.remove(player.getCommandSenderName());
    }

    public static boolean isDebugMode(EntityPlayer player)
    {
        return debugModePlayers.contains(player.getCommandSenderName());
    }

}
