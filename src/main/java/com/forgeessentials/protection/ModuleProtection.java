package com.forgeessentials.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.event.ClickEvent;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.protection.commands.CommandItemPermission;
import com.forgeessentials.protection.commands.CommandProtectionDebug;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerInitEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerPostInitEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.LoggingHandler;

@FEModule(name = "Protection", parentMod = ForgeEssentials.class, isCore = true, canDisable = false)
public class ModuleProtection
{

    public final static String BASE_PERM = "fe.protection";

    public final static String PERM_PVP = BASE_PERM + ".pvp";

    public final static String PERM_SLEEP = BASE_PERM + ".sleep";
    public final static String PERM_GAMEMODE = BASE_PERM + ".gamemode";
    public final static String PERM_INVENTORY_GROUP = BASE_PERM + ".inventorygroup";

    public final static String PERM_USE = BASE_PERM + ".use";
    public final static String PERM_BREAK = BASE_PERM + ".break";
    public final static String PERM_EXPLODE = BASE_PERM + ".explode";
    public final static String PERM_PLACE = BASE_PERM + ".place";
    public final static String PERM_TRAMPLE = BASE_PERM + ".trample";
    public final static String PERM_FIRE = BASE_PERM + ".fire";
    public final static String PERM_FIRE_DESTROY = PERM_FIRE + ".destroy";
    public final static String PERM_FIRE_SPREAD = PERM_FIRE + ".spread";
    public final static String PERM_INTERACT = BASE_PERM + ".interact";
    public final static String PERM_INTERACT_ENTITY = BASE_PERM + ".interact.entity";
    public final static String PERM_DAMAGE_TO = BASE_PERM + ".damageto";
    public final static String PERM_DAMAGE_BY = BASE_PERM + ".damageby";
    public final static String PERM_INVENTORY = BASE_PERM + ".inventory";
    public final static String PERM_EXIST = BASE_PERM + ".exist";
    public static final String PERM_CRAFT = BASE_PERM + ".craft";
    public final static String PERM_EXPLOSION = BASE_PERM + ".explosion";
    public final static String PERM_NEEDSFOOD = BASE_PERM + ".needsfood";
    public static final String PERM_PRESSUREPLATE = BASE_PERM + ".pressureplate";

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

    private static final DamageSource[] damageByTypes = new DamageSource[] {
        DamageSource.anvil,
        DamageSource.cactus,
        DamageSource.drown,
        DamageSource.fall,
        DamageSource.fallingBlock,
        DamageSource.generic,
        DamageSource.inFire,
        DamageSource.inWall,
        DamageSource.lava,
        DamageSource.magic,
        DamageSource.onFire,
        DamageSource.outOfWorld,
        DamageSource.starve,
        DamageSource.wither,
    };

    public static Map<UUID, String> debugModePlayers = new HashMap<>();

    /* ------------------------------------------------------------ */

    private ProtectionEventHandler protectionHandler;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        protectionHandler = new ProtectionEventHandler();

        FECommandManager.registerCommand(new CommandItemPermission());
        FECommandManager.registerCommand(new CommandProtectionDebug());
        // FECommandManager.registerCommand(new CommandPlaceblock());
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

    @SubscribeEvent
    public void registerPermissions(FEModuleServerInitEvent event)
    {
        // ----------------------------------------
        // Other
        APIRegistry.perms.registerPermission(PERM_SLEEP, PermissionLevel.TRUE, "Allow players to sleep in beds");
        APIRegistry.perms.registerPermission(PERM_NEEDSFOOD, PermissionLevel.TRUE, "If denied to a player, their hunger bar will not deplete.");
        APIRegistry.perms.registerPermission(PERM_PVP, PermissionLevel.TRUE, "If denied for at least one of two fighting players, PvP will be disabled");
        APIRegistry.perms.registerPermissionProperty(PERM_GAMEMODE, "-1", "Force gamemode (-1 = none / default, 0 = survival, 1 = creative, 2 = adventure)");
        APIRegistry.perms.registerPermissionProperty(PERM_INVENTORY_GROUP, "default",
                "Inventory group property - can be set to any identifier to separate inventories for certain regions");
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, PermissionLevel.TRUE, "Allow interacting with entities (villagers, dogs, horses)");
        APIRegistry.perms.registerPermission(PERM_EXPLOSION, PermissionLevel.TRUE, "Allows explosions to occur");
        APIRegistry.perms.registerPermission(PERM_PRESSUREPLATE, PermissionLevel.TRUE, "Prevent players from triggering pressure plates");
        APIRegistry.perms.registerPermission(PERM_FIRE_DESTROY, PermissionLevel.TRUE, "Allow fire to destroy blocks");
        APIRegistry.perms.registerPermission(PERM_FIRE_SPREAD, PermissionLevel.TRUE, "Allow fire to spread");

        // ----------------------------------------
        // Damage

        APIRegistry.perms.registerPermission(PERM_DAMAGE_TO + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow damaging entities");
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow getting hurt by entities");
        for (Class<?> entityClass : damageEntityClasses)
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_TO + "." + entityClass.getSimpleName(), PermissionLevel.TRUE);
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + entityClass.getSimpleName(), PermissionLevel.TRUE);
        }
        for (DamageSource dmgType : damageByTypes)
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + dmgType.getDamageType(), PermissionLevel.TRUE);
        }
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + ".explosion", PermissionLevel.TRUE);

        // ----------------------------------------
        // Register mobs
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN + Zone.ALL_PERMS, PermissionLevel.TRUE, "(global) Allow spawning of mobs");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + Zone.ALL_PERMS, PermissionLevel.TRUE,
                "(global) Allow natural spawning of mobs (random spawn)");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + Zone.ALL_PERMS, PermissionLevel.TRUE,
                "(global) Allow forced spawning of mobs (mob-spawners)");

        for (Entry<String, Class<? extends Entity>> e : ((Map<String, Class<? extends Entity>>) EntityList.stringToClassMapping).entrySet())
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + e.getKey(), PermissionLevel.TRUE);
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + e.getKey(), PermissionLevel.TRUE);
            }
        for (MobType mobType : MobType.values())
        {
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(false), PermissionLevel.TRUE);
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(true), PermissionLevel.TRUE);
            APIRegistry.perms.registerPermission(mobType.getDamageByPermission(), PermissionLevel.TRUE);
            APIRegistry.perms.registerPermission(mobType.getDamageToPermission(), PermissionLevel.TRUE);
        }

        // ----------------------------------------
        // Register items
        APIRegistry.perms.registerPermission(PERM_USE + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow using items");
        APIRegistry.perms.registerPermission(PERM_INVENTORY + Zone.ALL_PERMS, PermissionLevel.TRUE,
                "Allow having item in inventory. Item will be dropped if not allowed.");
        APIRegistry.perms.registerPermission(PERM_EXIST + Zone.ALL_PERMS, PermissionLevel.TRUE,
                "Allow having item in inventory. Item will be destroyed if not allowed.");
        APIRegistry.perms.registerPermission(PERM_CRAFT + Zone.ALL_PERMS, PermissionLevel.TRUE,
                "Allow crafting of items. Not necessarily works with modded crafting tables");
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
            if (!(item instanceof ItemBlock))
            {
                String itemPerm = "." + ServerUtil.getItemPermission(item) + Zone.ALL_PERMS;
                String itemName = getItemName(item);
                APIRegistry.perms.registerPermission(PERM_USE + itemPerm, PermissionLevel.TRUE, "USE " + itemName);
                APIRegistry.perms.registerPermission(PERM_CRAFT + itemPerm, PermissionLevel.TRUE, "CRAFT " + itemName);
                APIRegistry.perms.registerPermission(PERM_EXIST + itemPerm, PermissionLevel.TRUE, "EXIST " + itemName);
                APIRegistry.perms.registerPermission(PERM_INVENTORY + itemPerm, PermissionLevel.TRUE, "INVENTORY " + itemName);
            }

        // ----------------------------------------
        // Register blocks
        APIRegistry.perms.registerPermission(PERM_BREAK + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow breaking blocks");
        APIRegistry.perms.registerPermission(PERM_PLACE + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow placing blocks");
        APIRegistry.perms.registerPermission(PERM_TRAMPLE + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow trampling on blocks");
        APIRegistry.perms.registerPermission(PERM_EXPLODE + Zone.ALL_PERMS, PermissionLevel.TRUE, "(global) Allows blocks to explode");
        APIRegistry.perms.registerPermission(PERM_INTERACT + Zone.ALL_PERMS, PermissionLevel.TRUE, "Allow interacting with blocks (button, chest, workbench)");
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            String blockPerm = "." + ServerUtil.getBlockPermission(block) + Zone.ALL_PERMS;
            String blockName = block.getLocalizedName();
            APIRegistry.perms.registerPermission(PERM_BREAK + blockPerm, PermissionLevel.TRUE, "BREAK " + blockName);
            APIRegistry.perms.registerPermission(PERM_PLACE + blockPerm, PermissionLevel.TRUE, "PLACE " + blockName);
            APIRegistry.perms.registerPermission(PERM_TRAMPLE + blockPerm, PermissionLevel.TRUE, "PLACE " + blockName);
            APIRegistry.perms.registerPermission(PERM_INTERACT + blockPerm, PermissionLevel.TRUE, "INTERACT " + blockName);
            APIRegistry.perms.registerPermission(PERM_EXPLODE + blockPerm, PermissionLevel.TRUE, "EXPLODE " + blockName);
        }

        // ----------------------------------------
        // Register zone permissions
        APIRegistry.perms.registerPermissionDescription(ZONE, "Worldborder permissions");
        APIRegistry.perms.registerPermission(ZONE_KNOCKBACK, PermissionLevel.FALSE, "Deny players from entering this area");
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
    public void postServerStart(FEModuleServerPostInitEvent e)
    {
        TaskRegistry.scheduleRepeated(new TimerTask() {
            @Override
            public void run()
            {
                for (EntityPlayerMP p : ServerUtil.getPlayerList())
                    if (!APIRegistry.perms.checkPermission(p, PERM_NEEDSFOOD))
                        p.getFoodStats().addStats(20, 1.0F);
            }
        }, 60 * 1000);
    }

    /* ------------------------------------------------------------ */

    public static void setDebugMode(EntityPlayer player, String commandBase)
    {
        if (commandBase != null)
            debugModePlayers.put(player.getPersistentID(), commandBase);
        else
            debugModePlayers.remove(player.getPersistentID());
    }

    public static boolean isDebugMode(EntityPlayer player)
    {
        return debugModePlayers.containsKey(player.getPersistentID());
    }

    public static void debugPermission(EntityPlayer player, String permission)
    {
        if (player == null)
            return;
        String cmdBase = debugModePlayers.get(player.getPersistentID());
        if (cmdBase == null)
            return;

        ChatComponentTranslation msg = new ChatComponentTranslation(permission);
        msg.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmdBase + permission));
        msg.getChatStyle().setColor(ChatOutputHandler.chatNotificationColor);
        msg.getChatStyle().setUnderlined(true);
        ChatOutputHandler.sendMessage(player, msg);
    }

    /* ------------------------------------------------------------ */

    public static String getBlockPermission(Block block, int meta)
    {
        if (meta == 0 || meta == 32767)
            return ServerUtil.getBlockPermission(block);
        else
            return ServerUtil.getBlockPermission(block) + "." + meta;
    }

    public static String getBlockPermission(IBlockState blockState)
    {
        return getBlockPermission(blockState.getBlock(), blockState.getBlock().getMetaFromState(blockState));
    }

    public static String getBlockBreakPermission(IBlockState blockState)
    {
        return ModuleProtection.PERM_BREAK + "." + getBlockPermission(blockState);
    }

    public static String getBlockTramplePermission(IBlockState blockState)
    {
        return PERM_TRAMPLE + "." + getBlockPermission(blockState);
    }

    public static String getBlockPlacePermission(IBlockState blockState)
    {
        return ModuleProtection.PERM_PLACE + "." + getBlockPermission(blockState);
    }

    public static String getBlockInteractPermission(IBlockState blockState)
    {
        return ModuleProtection.PERM_INTERACT + "." + getBlockPermission(blockState);
    }

    public static String getBlockExplosionPermission(IBlockState blockState)
    {
        return ModuleProtection.PERM_EXPLODE + "." + getBlockPermission(blockState);
    }

    public static String getBlockBreakPermission(Block block, int meta)
    {
        return PERM_BREAK + "." + getBlockPermission(block, meta);
    }

    public static String getBlockTramplePermission(Block block, int meta)
    {
        return PERM_TRAMPLE + "." + getBlockPermission(block, meta);
    }

    public static String getBlockPlacePermission(Block block, int meta)
    {
        return PERM_PLACE + "." + getBlockPermission(block, meta);
    }

    public static String getBlockInteractPermission(Block block, int meta)
    {
        return PERM_INTERACT + "." + getBlockPermission(block, meta);
    }

    public static String getBlockExplosionPermission(Block block, int meta)
    {
        return PERM_EXPLODE + "." + getBlockPermission(block, meta);
    }

    /* ------------------------------------------------------------ */

    public static String getItemPermission(ItemStack stack, boolean checkMeta)
    {
        try
        {
            int dmg = stack.getItemDamage();
            if (!checkMeta || dmg == 0 || dmg == 32767)
                return ServerUtil.getItemPermission(stack.getItem());
            else
                return ServerUtil.getItemPermission(stack.getItem()) + "." + dmg;
        }
        catch (Exception e)
        {
            String msg;
            if (stack.getItem() == null)
                msg = "Error getting item permission. Stack item is null. Please report this error (except for TF) and try enabling FE safe-mode.";
            else
                msg = String.format("Error getting item permission for item %s. Please report this error and try enabling FE safe-mode.",
                        stack.getItem().getClass().getName());
            if (!ForgeEssentials.isSafeMode())
                throw new RuntimeException(msg, e);
            LoggingHandler.felog.error(msg);
            return "fe.error";
        }
    }

    public static String getItemPermission(ItemStack stack)
    {
        return getItemPermission(stack, true);
    }

    public static String getItemUsePermission(ItemStack stack)
    {
        return PERM_USE + "." + getItemPermission(stack);
    }

    public static String getItemBanPermission(ItemStack stack)
    {
        return PERM_EXIST + "." + getItemPermission(stack);
    }

    public static String getItemInventoryPermission(ItemStack stack)
    {
        return PERM_INVENTORY + "." + getItemPermission(stack);
    }

    /* ------------------------------------------------------------ */

    public static EntityPlayer getCraftingPlayer(InventoryCrafting inventory)
    {

        Container abstractContainer = inventory.eventHandler;
        if (abstractContainer instanceof ContainerPlayer)
        {
            ContainerPlayer container = (ContainerPlayer) abstractContainer;
            return container.thePlayer;
        }
        else if (abstractContainer instanceof ContainerWorkbench)
        {
            SlotCrafting slot = (SlotCrafting) abstractContainer.getSlot(0);
            return slot.thePlayer;
        }
        return null;
    }

    public static String getCraftingPermission(ItemStack stack)
    {
        return PERM_CRAFT + "." + getItemPermission(stack, true);
    }

    public static boolean canCraft(EntityPlayer player, ItemStack result)
    {
        if (player == null || result == null)
            return true;
        String permission = ModuleProtection.getCraftingPermission(result);
        debugPermission(player, permission);
        return PermissionManager.checkPermission(player, permission);
    }

}
