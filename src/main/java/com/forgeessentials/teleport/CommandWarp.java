package com.forgeessentials.teleport;

import java.util.Map;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.FunctionHelper;

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

    @Override
    public String getCommandName()
    {
        return "warp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/warp <name> [set|delete]: Set, delete or teleport to a warp point.";
    }

    @Override
    public String getPermissionNode()
    {
        return PERM;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void registerExtraPermissions()
    {
        APIRegistry.perms.registerPermission(PERM_SET, RegisteredPermValue.OP, "Allow setting warps");
        APIRegistry.perms.registerPermission(PERM_DELETE, RegisteredPermValue.OP, "Allow deleting warps");
        APIRegistry.perms.registerPermissionProperty(PERM_LIMIT, "10", "Maximal warp count");
        APIRegistry.perms.registerPermissionPropertyOp(PERM_LIMIT, "false");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    public static Map<String, Warp> getWarps()
    {
        return DataManager.getInstance().loadAll(Warp.class);
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm(getCommandUsage(arguments.sender));
            return;
        }

        Map<String, Warp> warps = getWarps();

        arguments.tabComplete(warps.keySet());
        String warpName = arguments.remove().toLowerCase();

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
                int limit = FunctionHelper.parseIntDefault(APIRegistry.perms.getUserPermissionProperty(arguments.ident, PERM_LIMIT), Integer.MAX_VALUE);
                if (warps.size() >= limit)
                    throw new TranslatedCommandException("You reached the warp limit");

                DataManager.getInstance().save(new Warp(arguments.senderPlayer), warpName);
                arguments.confirm(Translator.format("Set warp \"%s\" to current location", warpName));
                break;
            case "del":
            case "delete":
                arguments.checkPermission(PERM_DELETE);
                DataManager.getInstance().delete(Warp.class, warpName);
                arguments.confirm(Translator.format("Deleted warp \"%s\"", warpName));
                break;
            default:
                throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCommand);
            }
        }
    }

}
