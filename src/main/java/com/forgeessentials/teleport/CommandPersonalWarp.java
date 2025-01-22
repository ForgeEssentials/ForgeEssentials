package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.ServerUtil;

public class CommandPersonalWarp extends ParserCommandBase
{

    public static class PersonalWarp extends HashMap<String, WarpPoint>
    {
    }

    private static final String PERM = "fe.teleport.personalwarp";
    private static final String PERM_SET = PERM + ".set";
    private static final String PERM_DELETE = PERM + ".delete";
    private static final String PERM_LIMIT = PERM + ".max";

    @Override
    public String getCommandName()
    {
        return "pwarp";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "pw", "personalwarp" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/pwarp <name> [set|delete]: Set/delete/teleport to pers. warps";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_SET, PermissionLevel.OP, "Allow setting personal warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, PermissionLevel.OP, "Allow deleting personal warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal personal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
    }

    public static PersonalWarp getWarps(EntityPlayerMP player)
    {
        PersonalWarp warps = DataManager.getInstance().load(PersonalWarp.class, player.getPersistentID().toString());
        if (warps == null)
            warps = new PersonalWarp();
        return warps;
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/pwarp list: List personal warps");
            arguments.confirm(getCommandUsage(arguments.sender));
            return;
        }

        PersonalWarp warps = getWarps(arguments.senderPlayer);

        Set<String> completeList = new HashSet<>();
        completeList.add("list");
        completeList.addAll(warps.keySet());
        arguments.tabComplete(completeList);

        String warpName = arguments.remove().toLowerCase();

        if (warpName.equals("list"))
        {
            arguments.confirm("Warps: " + StringUtils.join(warps.keySet(), ", "));
            return;
        }

        if (arguments.isEmpty())
        {
            if (arguments.isTabCompletion)
                return;

            WarpPoint point = warps.get(warpName);
            if (point == null)
                throw new TranslatedCommandException("Warp by this name does not exist");
            TeleportHelper.teleport(arguments.senderPlayer, point);
        }
        else
        {
            arguments.tabComplete("set", "delete");
            if (arguments.isTabCompletion)
                return;

            String subCommand = arguments.remove().toLowerCase();
            switch (subCommand)
            {
            case "set":
                arguments.checkPermission(PERM_SET);

                // Check limit
                int limit = ServerUtil.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(arguments.ident, PERM_LIMIT), Integer.MAX_VALUE);
                if (warps.size() >= limit)
                    throw new TranslatedCommandException("You reached your personal warp limit");

                warps.put(warpName, new WarpPoint(arguments.senderPlayer));
                arguments.confirm("Set personal warp \"%s\" to current location", warpName);
                break;
            case "del":
            case "delete":
                arguments.checkPermission(PERM_DELETE);
                warps.remove(warpName);
                arguments.confirm("Deleted personal warp \"%s\"", warpName);
                break;
            default:
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
            }
            DataManager.getInstance().save(warps, arguments.senderPlayer.getPersistentID().toString());
        }
    }

}
