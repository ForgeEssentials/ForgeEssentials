package com.forgeessentials.protection.commands;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.block.Block;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.CommandParserArgs;

import cpw.mods.fml.common.registry.GameData;

public class CommandUpgradePermissions extends ParserCommandBase {

    @Override
    public String getCommandName()
    {
        return "upgradeoldpermissions";
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.protection.cmd.upgradeoldpermissions";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/upgradeoldpermissions";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        arguments.confirm("Upgrading permissions...");
        upgradePermissions(APIRegistry.perms.getServerZone());
        arguments.confirm("DONE!");
    }

    public static void upgradePermissions(ServerZone serverZone)
    {
        doUpgradePermissions(serverZone);
        for (WorldZone wz : serverZone.getWorldZones().values())
        {
            doUpgradePermissions(wz);
            for (AreaZone az : wz.getAreaZones())
            {
                doUpgradePermissions(az);
            }
        }
    }

    private static Pattern oldNamePattern = Pattern.compile("^(\\w+\\.\\w+)(\\.(?:\\d+|\\*))?$");

    private static String upgradeBlockId(String id)
    {
        Matcher match = oldNamePattern.matcher(id);
        if (!match.matches())
            return null;
        id = match.group(1);
        for (Block block : GameData.getBlockRegistry().typeSafeIterable())
            if (id.equals(block.getUnlocalizedName()))
                return GameData.getBlockRegistry().getNameForObject(block) + match.group(2);
        return null;
    }

    private static String upgradeItemId(String id)
    {
        Matcher match = oldNamePattern.matcher(id);
        if (!match.matches())
            return null;
        id = match.group(1);
        for (Item item : GameData.getItemRegistry().typeSafeIterable())
            if (id.equals(item.getUnlocalizedName()))
                return GameData.getItemRegistry().getNameForObject(item) + match.group(2);
        return null;
    }

    private static void doUpgradePermissions(Zone zone)
    {
        for (Entry<String, PermissionList> group : zone.getGroupPermissions().entrySet())
        {
            PermissionList newPerms = new PermissionList();
            for (Iterator<Map.Entry<String, String>> iterator = group.getValue().entrySet().iterator(); iterator.hasNext();)
            {
                Entry<String, String> permission = iterator.next();
                if (!permission.getKey().startsWith(ModuleProtection.BASE_PERM) || permission.getKey().contains(":"))
                    continue;
                if (permission.getKey().startsWith(ModuleProtection.PERM_BREAK))
                {
                    String newId = upgradeBlockId(permission.getKey().substring(ModuleProtection.PERM_BREAK.length() + 1));
                    if (newId != null)
                    {
                        iterator.remove();
                        newPerms.put(ModuleProtection.PERM_BREAK + "." + newId, permission.getValue());
                    }
                }
                else if (permission.getKey().startsWith(ModuleProtection.PERM_PLACE))
                {
                    String newId = upgradeBlockId(permission.getKey().substring(ModuleProtection.PERM_PLACE.length() + 1));
                    if (newId != null)
                    {
                        iterator.remove();
                        newPerms.put(ModuleProtection.PERM_PLACE + "." + newId, permission.getValue());
                    }
                }
                else if (permission.getKey().startsWith(ModuleProtection.PERM_INTERACT))
                {
                    String newId = upgradeBlockId(permission.getKey().substring(ModuleProtection.PERM_INTERACT.length() + 1));
                    if (newId != null)
                    {
                        iterator.remove();
                        newPerms.put(ModuleProtection.PERM_INTERACT + "." + newId, permission.getValue());
                    }
                }
                else if (permission.getKey().startsWith(ModuleProtection.PERM_USE))
                {
                    String newId = upgradeItemId(permission.getKey().substring(ModuleProtection.PERM_USE.length() + 1));
                    if (newId != null)
                    {
                        iterator.remove();
                        newPerms.put(ModuleProtection.PERM_USE + "." + newId, permission.getValue());
                    }
                }
            }
            group.getValue().putAll(newPerms);
        }
    }

}
