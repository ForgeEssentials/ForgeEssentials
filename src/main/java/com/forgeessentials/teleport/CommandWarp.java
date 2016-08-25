package com.forgeessentials.teleport;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
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

public class CommandWarp extends ParserCommandBase
{

    public static class Warp extends WarpPoint
    {
        public Warp(Entity entity)
        {
            super(entity);
        }
    }

    private static final String PERM = "fe.teleport.warp";
    private static final String PERM_SET = PERM + ".set";
    private static final String PERM_DELETE = PERM + ".delete";
    private static final String PERM_LIMIT = PERM + ".max";
    private static final String PERM_WARP = PERM + ".warp";

    @Override
    public String getCommandName()
    {
        return "fewarp";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "warp" };
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/warp <name> [set|delete]: Set/delete/teleport to a warp point";
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
        APIRegistry.perms.registerPermission(PERM_SET, PermissionLevel.OP, "Allow setting warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, PermissionLevel.OP, "Allow deleting warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
    }

    public static Map<String, Warp> getWarps()
    {
        return DataManager.getInstance().loadAll(Warp.class);
    }

    @Override
    public void parse(CommandParserArgs arguments) throws CommandException
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/warp list: List warps");
            arguments.confirm(getCommandUsage(arguments.sender));
            return;
        }

        Map<String, Warp> warps = getWarps();

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
            if (!arguments.hasPermission(PERM_WARP + "." + warpName))
                throw new TranslatedCommandException("You don't have permission to use this warp");
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
                    throw new TranslatedCommandException("You reached the warp limit");

                DataManager.getInstance().save(new Warp(arguments.senderPlayer), warpName);
                arguments.confirm("Set warp \"%s\" to current location", warpName);
                break;
            case "del":
            case "delete":
                arguments.checkPermission(PERM_DELETE);
                DataManager.getInstance().delete(Warp.class, warpName);
                arguments.confirm("Deleted warp \"%s\"", warpName);
                break;
            default:
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
            }
        }
    }

}
