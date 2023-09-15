package com.forgeessentials.jscripting.command;

import java.util.List;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.jscripting.ScriptUpgrader;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraftforge.server.permission.DefaultPermissionLevel;
import org.jetbrains.annotations.NotNull;

public class CommandJScript extends ForgeEssentialsCommandBuilder
{

    public CommandJScript(boolean enabled)
    {
        super(enabled);
    }

    @Override
    public @NotNull String getPrimaryAlias()
    {
        return "fescript";
    }

    @Override
    public String @NotNull [] getDefaultSecondaryAliases()
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
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        return baseBuilder.then(Commands.literal("list").executes(CommandContext -> execute(CommandContext, "list")))
                .then(Commands.literal("reload").executes(CommandContext -> execute(CommandContext, "reload")))
                .then(Commands.literal("upgrade").executes(CommandContext -> execute(CommandContext, "upgrade")));
    }

    @Override
    public int execute(CommandContext<CommandSource> ctx, String params) throws CommandSyntaxException
    {
        switch (params)
        {
        case "list":
            parseList(ctx);
            break;
        case "reload":
            parseReload(ctx);
            break;
        case "upgrade":
            ScriptUpgrader.upgradeOldScripts(ctx.getSource());
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void parseReload(CommandContext<CommandSource> ctx)
    {
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Reloading scripts...");
        ModuleJScripting.instance().reloadScripts(ctx.getSource());
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Done!");
    }

    private static void parseList(CommandContext<CommandSource> ctx)
    {
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Loaded scripts:");
        for (ScriptInstance script : ModuleJScripting.getScripts())
        {
            ChatOutputHandler.chatNotification(ctx.getSource(), script.getName());

            List<String> eventHandlers = script.getEventHandlers();
            if (!eventHandlers.isEmpty())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "  Registered events:");
                for (String eventType : eventHandlers)
                	ChatOutputHandler.chatConfirmation(ctx.getSource(), "    " + eventType);
            }

            List<CommandJScriptCommand> commands = script.getCommands();
            if (!commands.isEmpty())
            {
                ChatOutputHandler.chatConfirmation(ctx.getSource(), "  Registered commands:");
                for (CommandJScriptCommand command : commands)
                	ChatOutputHandler.chatConfirmation(ctx.getSource(), "    /" + command.getName());
            }
        }
    }
}