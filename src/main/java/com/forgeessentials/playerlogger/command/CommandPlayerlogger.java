package com.forgeessentials.playerlogger.command;

import javax.persistence.TypedQuery;

import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ParserCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.playerlogger.ModulePlayerLogger;
import com.forgeessentials.playerlogger.PlayerLogger;
import com.forgeessentials.playerlogger.entity.Action;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.OutputHandler;

public class CommandPlayerlogger extends ParserCommandBase
{

    @Override
    public String getCommandName()
    {
        return "pl";
    }

    @Override
    public String getPermissionNode()
    {
        return ModulePlayerLogger.PERM_COMMAND + ".pl";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/pl stats";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public void parse(CommandParserArgs arguments)
    {
        if (arguments.isEmpty())
        {
            arguments.confirm("/pl stats: Show playerlogger stats");
            return;
        }
        arguments.tabComplete("stats");
        String subCmd = arguments.remove().toLowerCase();
        switch (subCmd)
        {
        case "stats":
            if (arguments.isTabCompletion)
                return;
            showStats(arguments.sender);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subCmd);
        }
    }

    public static void showStats(ICommandSender sender)
    {
        PlayerLogger logger = ModulePlayerLogger.getLogger();
        synchronized (logger)
        {
            TypedQuery<Long> qActionCount = logger.buildCountQuery(Action.class, null, null);
            long actionCount = qActionCount.getSingleResult();
            OutputHandler.chatConfirmation(sender, String.format("Logged action count: %s", actionCount));
        }
    }

}
