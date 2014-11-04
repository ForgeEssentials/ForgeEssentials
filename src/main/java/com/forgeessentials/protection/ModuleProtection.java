package com.forgeessentials.protection;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
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
    // public final static String PERMPROP_ZONE_GAMEMODE = BASE_PERM + ".data.zonegamemode";

    public final static String PERM_USE = BASE_PERM + ".use";
    public final static String PERM_BREAK = BASE_PERM + ".break";
    public final static String PERM_PLACE = BASE_PERM + ".place";
    public final static String PERM_INTERACT = BASE_PERM + ".interact";
    public final static String PERM_INTERACT_ENTITY = BASE_PERM + ".interact.entity";
    public final static String PERM_DAMAGE_TO = BASE_PERM + ".damageto";
    public final static String PERM_DAMAGE_BY = BASE_PERM + ".damageby";
    public final static String PERM_BAN = BASE_PERM + ".ban";

    private final static String PERM_OVERRIDE = BASE_PERM + ".override";
    public final static String PERM_OVERRIDE_USE = PERM_OVERRIDE + ".use";
    public final static String PERM_OVERRIDE_BREAK = PERM_OVERRIDE + ".break";
    public final static String PERM_OVERRIDE_PLACE = PERM_OVERRIDE + ".place";
    public final static String PERM_OVERRIDE_INTERACT = PERM_OVERRIDE + ".interact";
    public final static String PERM_OVERRIDE_INTERACT_ENTITY = PERM_OVERRIDE + ".interact.entity";
    public final static String PERM_OVERRIDE_BANNEDITEMS = PERM_OVERRIDE + ".banneditems";

    public final static String PERM_MOBSPAWN = BASE_PERM + ".mobspawn";
    public final static String PERM_MOBSPAWN_NATURAL = PERM_MOBSPAWN + ".natural";
    public final static String PERM_MOBSPAWN_FORCED = PERM_MOBSPAWN + ".forced";

    public static Set<String> debugModePlayers = new HashSet<>();

    @SuppressWarnings("unused")
    private ProtectionEventHandler protectionHandler;

    @SubscribeEvent
    public void load(FEModuleInitEvent e)
    {
        protectionHandler = new ProtectionEventHandler();
    }

    @SubscribeEvent
    public void serverStarting(FEModuleServerInitEvent e)
    {
        FunctionHelper.registerServerCommand(new CommandItemPermission());
        FunctionHelper.registerServerCommand(new CommandProtectionDebug());
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
        FunctionHelper.registerServerCommand(new ProtectCommand());

        APIRegistry.perms.registerPermission(PERM_PVP, RegisteredPermValue.TRUE, "Allow PvP");
        // APIRegistry.perms.registerPermissionProperty(PERMPROP_ZONE_GAMEMODE, Integer.toString(0));

        APIRegistry.perms.registerPermission(PERM_USE, RegisteredPermValue.TRUE, "Allow using items");
        APIRegistry.perms.registerPermission(PERM_BREAK, RegisteredPermValue.TRUE, "Allow breaking blocks");
        APIRegistry.perms.registerPermission(PERM_PLACE, RegisteredPermValue.TRUE, "Allow placing blocks");
        APIRegistry.perms.registerPermission(PERM_INTERACT, RegisteredPermValue.TRUE, "Allow interacting with blocks");
        APIRegistry.perms.registerPermission(PERM_INTERACT_ENTITY, RegisteredPermValue.TRUE, "Allow interacting with entities");
        APIRegistry.perms.registerPermission(PERM_DAMAGE_TO, RegisteredPermValue.TRUE, "Allow damaging entities");
        APIRegistry.perms.registerPermission(PERM_DAMAGE_BY, RegisteredPermValue.TRUE, "Allow getting hurt by entities");

        APIRegistry.perms.registerPermission(PERM_OVERRIDE + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.OP, "Override protection permissions");
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_USE, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_BREAK, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_PLACE, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_INTERACT, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_INTERACT_ENTITY, RegisteredPermValue.OP);
        APIRegistry.perms.registerPermission(PERM_OVERRIDE_BANNEDITEMS, RegisteredPermValue.OP);

        APIRegistry.perms.registerPermission(PERM_MOBSPAWN + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED, RegisteredPermValue.TRUE);

        // ----------------------------------------
        // Register mobs
        for (Entry<String, Class<?>> e : (Set<Entry<String, Class<?>>>) EntityList.stringToClassMapping.entrySet())
            if (EntityLiving.class.isAssignableFrom(e.getValue()))
            {
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + e.getKey(), RegisteredPermValue.TRUE);
                APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + e.getKey(), RegisteredPermValue.TRUE);
            }
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_NATURAL + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_MOBSPAWN_FORCED + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);

        // ----------------------------------------
        // Register items
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
            if (!(item instanceof ItemBlock))
            {
                String itemPerm = "." + item.getUnlocalizedName() + "." + IPermissionsHelper.PERMISSION_ASTERIX;
                APIRegistry.perms.registerPermission(PERM_USE + itemPerm, RegisteredPermValue.TRUE, "USE " + getItemName(item));
                APIRegistry.perms.registerPermission(PERM_BAN + itemPerm, RegisteredPermValue.TRUE, "USE " + getItemName(item));
            }

        APIRegistry.perms.registerPermission(PERM_USE + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_BAN + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);

        // ----------------------------------------
        // Register blocks
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
        {
            String blockPerm = "." + block.getUnlocalizedName() + "." + IPermissionsHelper.PERMISSION_ASTERIX;
            APIRegistry.perms.registerPermission(PERM_BREAK + blockPerm, RegisteredPermValue.TRUE, "BREAK " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_PLACE + blockPerm, RegisteredPermValue.TRUE, "PLACE " + block.getLocalizedName());
            APIRegistry.perms.registerPermission(PERM_INTERACT + blockPerm, RegisteredPermValue.TRUE, "INTERACT " + block.getLocalizedName());
        }
        APIRegistry.perms.registerPermission(PERM_BREAK + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_PLACE + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
        APIRegistry.perms.registerPermission(PERM_INTERACT + "." + IPermissionsHelper.PERMISSION_ASTERIX, RegisteredPermValue.TRUE);
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
