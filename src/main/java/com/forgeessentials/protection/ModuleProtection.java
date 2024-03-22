package com.forgeessentials.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.commands.registration.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.protection.commands.CommandItemPermission;
import com.forgeessentials.protection.commands.CommandProtectionDebug;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.ElderGuardian;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.Giant;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.monster.hoglin.Hoglin;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.entity.monster.Illusioner;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.monster.Silverfish;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.entity.monster.Zoglin;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.ambient.Bat;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.animal.PolarBear;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Squid;
import net.minecraft.world.entity.monster.Strider;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.animal.horse.Donkey;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.entity.animal.horse.Mule;
import net.minecraft.world.entity.animal.horse.SkeletonHorse;
import net.minecraft.world.entity.animal.horse.ZombieHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

@FEModule(name = "Protection", parentMod = ForgeEssentials.class, isCore = true, canDisable = false, version=ForgeEssentials.CURRENT_MODULE_VERSION)
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

	public final static String COMMANDBLOCK_PERM = "mc.commandblock";

    public static final String MSG_ZONE_DENIED = "You are not allowed to enter this area!";

    private static final Class<?>[] damageEntityClasses = new Class<?>[] {
            // EntityTradable
            Villager.class, WanderingTrader.class,
            // EntityAnimal
            Chicken.class, Cow.class, MushroomCow.class, Pig.class, Sheep.class,
            Rabbit.class, PolarBear.class, Panda.class, Fox.class, Bee.class,
            Bat.class,
            // EntityRidable
            Strider.class, Horse.class, Donkey.class, Llama.class, Mule.class,
            SkeletonHorse.class, ZombieHorse.class,
            // EntityTameable
            Ocelot.class, Wolf.class, Parrot.class, Cat.class,
            // EntityMob
            Blaze.class, Creeper.class, EnderMan.class, Giant.class, Silverfish.class,
            Skeleton.class, Spider.class, Witch.class, WitherBoss.class, Zombie.class,
            Piglin.class, PiglinBrute.class, CaveSpider.class, Drowned.class,
            ElderGuardian.class, Endermite.class, Evoker.class, Ghast.class,
            Guardian.class, Hoglin.class, Husk.class, Illusioner.class, MagmaCube.class,
            Phantom.class, Pillager.class, Ravager.class, Shulker.class, Slime.class,
            Stray.class, Vex.class, Vindicator.class, Zoglin.class, Zombie.class,
            ZombieVillager.class, ZombifiedPiglin.class,
            // EntityGolem
            IronGolem.class, SnowGolem.class,
            // EntityWaterMob
            Squid.class, Salmon.class, TropicalFish.class, Cod.class, Turtle.class,
            Dolphin.class, Pufferfish.class,
            // BossEntity
            WitherBoss.class, EnderDragon.class,
            /* -- end of list -- */
    };

    private static final DamageSource[] damageByTypes = new DamageSource[] { DamageSource.IN_FIRE,
            DamageSource.LIGHTNING_BOLT, DamageSource.ON_FIRE, DamageSource.LAVA, DamageSource.HOT_FLOOR,
            DamageSource.IN_WALL, DamageSource.CRAMMING, DamageSource.DROWN, DamageSource.STARVE, DamageSource.CACTUS,
            DamageSource.FALL, DamageSource.FLY_INTO_WALL, DamageSource.OUT_OF_WORLD, DamageSource.GENERIC,
            DamageSource.MAGIC, DamageSource.WITHER, DamageSource.ANVIL, DamageSource.FALLING_BLOCK,
            DamageSource.DRAGON_BREATH, DamageSource.DRY_OUT, DamageSource.SWEET_BERRY_BUSH, };

    public static Map<UUID, String> debugModePlayers = new HashMap<>();

    /* ------------------------------------------------------------ */

    public ProtectionEventHandler protectionHandler = new ProtectionEventHandler();

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event)
    {
        FECommandManager.registerCommand(new CommandItemPermission(true), event.getDispatcher());
        FECommandManager.registerCommand(new CommandProtectionDebug(true), event.getDispatcher());
        // FECommandManager.registerCommand(new CommandPlaceblock());
    }

    @SubscribeEvent
    public void registerPermissions(FEModuleServerStartingEvent event)
    {
        // ----------------------------------------
        // Other
        APIRegistry.perms.registerPermission(PERM_SLEEP, DefaultPermissionLevel.ALL, "Allow players to sleep in beds");
        APIRegistry.perms.registerPermission(PERM_NEEDSFOOD, DefaultPermissionLevel.ALL,
                "If denied to a player, their hunger bar will not deplete.");
        APIRegistry.perms.registerPermission(PERM_PVP, DefaultPermissionLevel.ALL,
                "If denied for at least one of two fighting players, PvP will be disabled");
        APIRegistry.perms.registerPermissionProperty(PERM_GAMEMODE, "-1",
                "Force gamemode (-1 = none / default, 0 = survival, 1 = creative, 2 = adventure)");
        APIRegistry.perms.registerPermissionProperty(PERM_INVENTORY_GROUP, "default",
                "Inventory group property - can be set to any identifier to separate inventories for certain regions");
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, DefaultPermissionLevel.ALL,
                "Allow interacting with entities (villagers, dogs, horses)");
        APIRegistry.perms.registerPermission(PERM_EXPLOSION, DefaultPermissionLevel.ALL, "Allows explosions to occur");
        APIRegistry.perms.registerPermission(PERM_PRESSUREPLATE, DefaultPermissionLevel.ALL,
                "Prevent players from triggering pressure plates");
        APIRegistry.perms.registerPermission(PERM_FIRE_DESTROY, DefaultPermissionLevel.ALL,
                "Allow fire to destroy blocks");
        APIRegistry.perms.registerPermission(PERM_FIRE_SPREAD, DefaultPermissionLevel.ALL, "Allow fire to spread");

        // ----------------------------------------
        // Damage

        APIRegistry.perms.registerPermission(PERM_DAMAGE_TO + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow damaging entities");
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow getting hurt by entities");
        for (Entry<ResourceKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_TO + "." + e.getValue().getRegistryName().getPath(),
                    DefaultPermissionLevel.ALL, "");
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + e.getValue().getRegistryName().getPath(),
                    DefaultPermissionLevel.ALL, "");
        }
        for (DamageSource dmgType : damageByTypes)
        {
            APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + "." + dmgType.msgId, DefaultPermissionLevel.ALL, "");
        }
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY + ".explosion", DefaultPermissionLevel.ALL, "");

        // ----------------------------------------
        // Register mobs
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "(global) Allow spawning of mobs");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "(global) Allow natural spawning of mobs (random spawn)");
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "(global) Allow forced spawning of mobs (mob-spawners)");

        for (Entry<ResourceKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
        {
            APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + e.getValue().getRegistryName().getPath(),
                    DefaultPermissionLevel.ALL, "");
            APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + e.getValue().getRegistryName().getPath(),
                    DefaultPermissionLevel.ALL, "");
        }
        for (MobType mobType : MobType.values())
        {
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(false), DefaultPermissionLevel.ALL, "");
            APIRegistry.perms.registerPermission(mobType.getSpawnPermission(true), DefaultPermissionLevel.ALL, "");
            APIRegistry.perms.registerPermission(mobType.getDamageByPermission(), DefaultPermissionLevel.ALL, "");
            APIRegistry.perms.registerPermission(mobType.getDamageToPermission(), DefaultPermissionLevel.ALL, "");
        }

        // ----------------------------------------
        // Register items
        APIRegistry.perms.registerPermission(PERM_USE + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow using items");
        APIRegistry.perms.registerPermission(PERM_INVENTORY + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow having item in inventory. Item will be dropped if not allowed.");
        APIRegistry.perms.registerPermission(PERM_EXIST + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow having item in inventory. Item will be destroyed if not allowed.");
        APIRegistry.perms.registerPermission(PERM_CRAFT + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow crafting of items. Not necessarily works with modded crafting tables");
        for (Item item : ForgeRegistries.ITEMS.getValues())
            if (!(item instanceof BlockItem))
            {
                String itemPerm = "." + ServerUtil.getItemPermission(item) + Zone.ALL_PERMS;
                String itemName;
                try
                {
                    itemName = item.getDescriptionId().toString();
                }
                catch (Exception | NoClassDefFoundError e)
                {
                    itemName = item.getRegistryName().toString();
                }
                APIRegistry.perms.registerPermission(PERM_USE + itemPerm, DefaultPermissionLevel.ALL,
                        "USE " + itemName);
                APIRegistry.perms.registerPermission(PERM_CRAFT + itemPerm, DefaultPermissionLevel.ALL,
                        "CRAFT " + itemName);
                APIRegistry.perms.registerPermission(PERM_EXIST + itemPerm, DefaultPermissionLevel.ALL,
                        "EXIST " + itemName);
                APIRegistry.perms.registerPermission(PERM_INVENTORY + itemPerm, DefaultPermissionLevel.ALL,
                        "INVENTORY " + itemName);
            }

        // ----------------------------------------
        // Register blocks
        APIRegistry.perms.registerPermission(PERM_BREAK + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow breaking blocks");
        APIRegistry.perms.registerPermission(PERM_PLACE + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow placing blocks");
        APIRegistry.perms.registerPermission(PERM_TRAMPLE + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow trampling on blocks");
        APIRegistry.perms.registerPermission(PERM_EXPLODE + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "(global) Allows blocks to explode");
        APIRegistry.perms.registerPermission(PERM_INTERACT + Zone.ALL_PERMS, DefaultPermissionLevel.ALL,
                "Allow interacting with blocks (button, chest, workbench)");
        for (Block block : ForgeRegistries.BLOCKS.getValues())
        {
            String blockPerm = "." + ServerUtil.getBlockPermission(block) + Zone.ALL_PERMS;
            String blockName;
            try
            {
                blockName = block.getName().toString();
            }
            catch (Throwable e)
            {
                blockName = block.getRegistryName().toString();
            }
            APIRegistry.perms.registerPermission(PERM_BREAK + blockPerm, DefaultPermissionLevel.ALL,
                    "BREAK " + blockName);
            APIRegistry.perms.registerPermission(PERM_PLACE + blockPerm, DefaultPermissionLevel.ALL,
                    "PLACE " + blockName);
            APIRegistry.perms.registerPermission(PERM_TRAMPLE + blockPerm, DefaultPermissionLevel.ALL,
                    "PLACE " + blockName);
            APIRegistry.perms.registerPermission(PERM_INTERACT + blockPerm, DefaultPermissionLevel.ALL,
                    "INTERACT " + blockName);
            APIRegistry.perms.registerPermission(PERM_EXPLODE + blockPerm, DefaultPermissionLevel.ALL,
                    "EXPLODE " + blockName);
        }

        // ----------------------------------------
        // Register zone permissions
        APIRegistry.perms.registerPermissionDescription(ZONE, "Worldborder permissions");
        APIRegistry.perms.registerPermission(ZONE_KNOCKBACK, DefaultPermissionLevel.NONE,
                "Deny players from entering this area");
        APIRegistry.perms.registerPermissionProperty(ZONE_DAMAGE, null,
                "Apply this amount of damage to players, if they are in this area");
        APIRegistry.perms.registerPermissionProperty(ZONE_DAMAGE_INTERVAL, "1000",
                "Time interval in milliseconds for applying damage-effect. Zero = once only.");
        APIRegistry.perms.registerPermissionProperty(ZONE_COMMAND, null,
                "Execute this command if a player enters the area");
        APIRegistry.perms.registerPermissionProperty(ZONE_COMMAND_INTERVAL, "0",
                "Time interval in milliseconds for executing command. Zero = once only.");
        APIRegistry.perms.registerPermissionProperty(ZONE_POTION, null,
                "Apply potion effects to players who enter this area. Comma separated list of \"ID:duration:amplifier\" pairs. See http://www.minecraftwiki.net/wiki/Potion_effects#Parameters");
        APIRegistry.perms.registerPermissionProperty(ZONE_POTION_INTERVAL, "2000",
                "Time interval in milliseconds for applying potion-effects. Zero = once only.");
    }

    @SubscribeEvent
    public void postServerStart(FEModuleServerStartedEvent e)
    {
        TaskRegistry.scheduleRepeated(new TimerTask() {
            @Override
            public void run()
            {
                for (ServerPlayer p : ServerUtil.getPlayerList())
                    if (!APIRegistry.perms.checkPermission(p, PERM_NEEDSFOOD))
                        p.getFoodData().eat(20, 1.0F);
            }
        }, 60 * 1000);
    }

    /* ------------------------------------------------------------ */

    public static void setDebugMode(Player player, String commandBase)
    {
        if (commandBase != null)
            debugModePlayers.put(player.getGameProfile().getId(), commandBase);
        else
            debugModePlayers.remove(player.getGameProfile().getId());
    }

    public static boolean isDebugMode(Player player)
    {
        return debugModePlayers.containsKey(player.getGameProfile().getId());
    }

    public static void debugPermission(Player player, String permission)
    {
        if (player == null)
            return;
        String cmdBase = debugModePlayers.get(player.getGameProfile().getId());
        if (cmdBase == null)
            return;

        BaseComponent msg = new TextComponent(permission);
        ClickEvent click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmdBase + permission);
        msg.withStyle((style) -> style.withClickEvent(click));
        msg.withStyle(ChatFormatting.UNDERLINE);
        msg.withStyle(ChatOutputHandler.chatNotificationColor);
        ChatOutputHandler.sendMessage(player.createCommandSourceStack(), msg);
    }

    /* ------------------------------------------------------------ */

    public static String getBlockPermission(Block block)
    {
        return ServerUtil.getBlockPermission(block);
    }

    public static String getBlockPermission(BlockState blockState)
    {
        return getBlockPermission(blockState.getBlock());
    }

    public static String getBlockBkPermission(BlockState blockState)
    {
        return ModuleProtection.PERM_BREAK + "." + getBlockPermission(blockState);
    }

    public static String getBlockTramplePermission(BlockState blockState)
    {
        return PERM_TRAMPLE + "." + getBlockPermission(blockState);
    }

    public static String getBlockPlacePermission(BlockState blockState)
    {
        return ModuleProtection.PERM_PLACE + "." + getBlockPermission(blockState);
    }

    public static String getBlockInteractPermission(BlockState blockState)
    {
        return ModuleProtection.PERM_INTERACT + "." + getBlockPermission(blockState);
    }

    public static String getBlockExplosionPermission(BlockState blockState)
    {
        return ModuleProtection.PERM_EXPLODE + "." + getBlockPermission(blockState);
    }

    public static String getBlockBreakPermission(Block block)
    {
        return PERM_BREAK + "." + getBlockPermission(block);
    }

    public static String getBlockTramplePermission(Block block)
    {
        return PERM_TRAMPLE + "." + getBlockPermission(block);
    }

    public static String getBlockPlacePermission(Block block)
    {
        return PERM_PLACE + "." + getBlockPermission(block);
    }

    public static String getBlockInteractPermission(Block block)
    {
        return PERM_INTERACT + "." + getBlockPermission(block);
    }

    public static String getBlockExplosionPermission(Block block)
    {
        return PERM_EXPLODE + "." + getBlockPermission(block);
    }

    /* ------------------------------------------------------------ */

    public static String getItemPermission(ItemStack stack)
    {
        try
        {
            return ServerUtil.getItemPermission(stack.getItem());
        }
        catch (Exception e)
        {
            String msg;
            if (stack == ItemStack.EMPTY)
                msg = "Error getting item permission. Stack item is null. Please report this error (except for TF) and try enabling FE safe-mode.";
            else
                msg = String.format(
                        "Error getting item permission for item %s. Please report this error and try enabling FE safe-mode.",
                        stack.getItem().getClass().getName());
            if (!ForgeEssentials.isSafeMode())
                throw new RuntimeException(msg, e);
            LoggingHandler.felog.error(msg);
            return "fe.error";
        }
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
    /*
     * public static PlayerEntity getCraftingPlayer(CraftingInventory inventory) {
     * 
     * Container abstractContainer = inventory; if (abstractContainer instanceof PlayerContainer) { PlayerContainer container = (PlayerContainer) abstractContainer; return
     * container.; } else if (abstractContainer instanceof WorkbenchContainer) { CraftingResultSlot slot = (CraftingResultSlot) abstractContainer.getSlot(0); return slot.player; }
     * return null; }
     */

    public static String getCraftingPermission(ItemStack stack)
    {
        return PERM_CRAFT + "." + getItemPermission(stack);
    }

    public static boolean canCraft(Player player, ItemStack result)
    {
        if (player == null || result == null)
            return true;
        String permission = ModuleProtection.getCraftingPermission(result);
        debugPermission(player, permission);
        return APIRegistry.perms.checkPermission(player, permission);
    }

}
