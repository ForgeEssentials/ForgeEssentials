package com.forgeessentials.protection;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimerTask;
import java.util.UUID;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.ForgeEssentials;
import com.forgeessentials.core.misc.FECommandManager;
import com.forgeessentials.core.misc.TaskRegistry;
import com.forgeessentials.core.moduleLauncher.FEModule;
import com.forgeessentials.protection.commands.CommandItemPermission;
import com.forgeessentials.protection.commands.CommandProtectionDebug;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartedEvent;
import com.forgeessentials.util.events.FEModuleEvent.FEModuleServerStartingEvent;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.forgeessentials.util.output.logger.LoggingHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.entity.monster.ElderGuardianEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.EndermiteEntity;
import net.minecraft.entity.monster.EvokerEntity;
import net.minecraft.entity.monster.GhastEntity;
import net.minecraft.entity.monster.GiantEntity;
import net.minecraft.entity.monster.GuardianEntity;
import net.minecraft.entity.monster.HoglinEntity;
import net.minecraft.entity.monster.HuskEntity;
import net.minecraft.entity.monster.IllusionerEntity;
import net.minecraft.entity.monster.MagmaCubeEntity;
import net.minecraft.entity.monster.PhantomEntity;
import net.minecraft.entity.monster.PillagerEntity;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.entity.monster.SilverfishEntity;
import net.minecraft.entity.monster.SkeletonEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.StrayEntity;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.entity.monster.VindicatorEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.monster.ZoglinEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.entity.monster.ZombifiedPiglinEntity;
import net.minecraft.entity.monster.piglin.PiglinBruteEntity;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.ChickenEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.MooshroomEntity;
import net.minecraft.entity.passive.OcelotEntity;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.passive.SnowGolemEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.StriderEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.passive.fish.CodEntity;
import net.minecraft.entity.passive.fish.PufferfishEntity;
import net.minecraft.entity.passive.fish.SalmonEntity;
import net.minecraft.entity.passive.fish.TropicalFishEntity;
import net.minecraft.entity.passive.horse.DonkeyEntity;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.passive.horse.MuleEntity;
import net.minecraft.entity.passive.horse.SkeletonHorseEntity;
import net.minecraft.entity.passive.horse.ZombieHorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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

	public final static String COMMANDBLOCK_PERM = "mc.commandblock";

    public static final String MSG_ZONE_DENIED = "You are not allowed to enter this area!";

    private static final Class<?>[] damageEntityClasses = new Class<?>[] {
            // EntityTradable
            VillagerEntity.class, WanderingTraderEntity.class,
            // EntityAnimal
            ChickenEntity.class, CowEntity.class, MooshroomEntity.class, PigEntity.class, SheepEntity.class,
            RabbitEntity.class, PolarBearEntity.class, PandaEntity.class, FoxEntity.class, BeeEntity.class,
            BatEntity.class,
            // EntityRidable
            StriderEntity.class, HorseEntity.class, DonkeyEntity.class, LlamaEntity.class, MuleEntity.class,
            SkeletonHorseEntity.class, ZombieHorseEntity.class,
            // EntityTameable
            OcelotEntity.class, WolfEntity.class, ParrotEntity.class, CatEntity.class,
            // EntityMob
            BlazeEntity.class, CreeperEntity.class, EndermanEntity.class, GiantEntity.class, SilverfishEntity.class,
            SkeletonEntity.class, SpiderEntity.class, WitchEntity.class, WitherEntity.class, ZombieEntity.class,
            PiglinEntity.class, PiglinBruteEntity.class, CaveSpiderEntity.class, DrownedEntity.class,
            ElderGuardianEntity.class, EndermiteEntity.class, EvokerEntity.class, GhastEntity.class,
            GuardianEntity.class, HoglinEntity.class, HuskEntity.class, IllusionerEntity.class, MagmaCubeEntity.class,
            PhantomEntity.class, PillagerEntity.class, RavagerEntity.class, ShulkerEntity.class, SlimeEntity.class,
            StrayEntity.class, VexEntity.class, VindicatorEntity.class, ZoglinEntity.class, ZombieEntity.class,
            ZombieVillagerEntity.class, ZombifiedPiglinEntity.class,
            // EntityGolem
            IronGolemEntity.class, SnowGolemEntity.class,
            // EntityWaterMob
            SquidEntity.class, SalmonEntity.class, TropicalFishEntity.class, CodEntity.class, TurtleEntity.class,
            DolphinEntity.class, PufferfishEntity.class,
            // BossEntity
            WitherEntity.class, EnderDragonEntity.class,
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

    private ProtectionEventHandler protectionHandler = new ProtectionEventHandler();

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
        for (Entry<RegistryKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
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

        for (Entry<RegistryKey<EntityType<?>>, EntityType<?>> e : ForgeRegistries.ENTITIES.getEntries())
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
                for (ServerPlayerEntity p : ServerUtil.getPlayerList())
                    if (!APIRegistry.perms.checkPermission(p, PERM_NEEDSFOOD))
                        p.getFoodData().eat(20, 1.0F);
            }
        }, 60 * 1000);
    }

    /* ------------------------------------------------------------ */

    public static void setDebugMode(PlayerEntity player, String commandBase)
    {
        if (commandBase != null)
            debugModePlayers.put(player.getGameProfile().getId(), commandBase);
        else
            debugModePlayers.remove(player.getGameProfile().getId());
    }

    public static boolean isDebugMode(PlayerEntity player)
    {
        return debugModePlayers.containsKey(player.getGameProfile().getId());
    }

    public static void debugPermission(PlayerEntity player, String permission)
    {
        if (player == null)
            return;
        String cmdBase = debugModePlayers.get(player.getGameProfile().getId());
        if (cmdBase == null)
            return;

        TextComponent msg = new StringTextComponent(permission);
        ClickEvent click = new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmdBase + permission);
        msg.withStyle((style) -> style.withClickEvent(click));
        msg.withStyle(TextFormatting.UNDERLINE);
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

    public static boolean canCraft(PlayerEntity player, ItemStack result)
    {
        if (player == null || result == null)
            return true;
        String permission = ModuleProtection.getCraftingPermission(result);
        debugPermission(player, permission);
        return APIRegistry.perms.checkPermission(player, permission);
    }

}
