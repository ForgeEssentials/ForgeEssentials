package com.forgeessentials.jscripting.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.ScriptUpgrader;
import com.forgeessentials.util.CommandParserArgs;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandJScript extends BaseCommand
{

    public CommandJScript(String name, int permissionLevel, boolean enabled)
    {
        super(enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "fescript";
    }

    @Override
    public String[] getDefaultSecondaryAliases()
    {
        return new String[] { "script" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleJScripting.PERM + ".manage";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {

        arguments.tabComplete("list", "reload", "upgrade");
        String subcmd = arguments.remove().toLowerCase();
        switch (subcmd)
        {
        case "list":
            parseList(arguments);
            break;
        case "reload":
            parseReload(arguments);
            break;
        case "upgrade":
            if (arguments.isTabCompletion)
                return;
            ScriptUpgrader.upgradeOldScripts(arguments.sender);
            break;
        default:
            throw new TranslatedCommandException(FEPermissions.MSG_UNKNOWN_SUBCOMMAND, subcmd);
        }
    }

    private static void parseReload(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        arguments.confirm("Reloading scripts...");
        ModuleJScripting.instance().reloadScripts(arguments.sender);
        arguments.confirm("Done!");
    }

    private static void parseList(CommandParserArgs arguments)
    {
        if (arguments.isTabCompletion)
            return;
        arguments.confirm("Loaded scripts:");
        for (ScriptInstance script : ModuleJScripting.getScripts())
        {
            arguments.notify(script.getName());

            List<String> eventHandlers = script.getEventHandlers();
            if (!eventHandlers.isEmpty())
            {
                arguments.confirm("  Registered events:");
                for (String eventType : eventHandlers)
                    arguments.sendMessage("    " + eventType);
            }

            List<CommandJScriptCommand> commands = script.getCommands();
            if (!commands.isEmpty())
            {
                arguments.confirm("  Registered commands:");
                for (CommandJScriptCommand command : commands)
                    arguments.sendMessage("    /" + command.getName());
            }
        }
    }
}