package com.forgeessentials.jscripting.command;

import java.util.List;
import java.util.Set;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBuilder;
import com.forgeessentials.jscripting.ModuleJScripting;
import com.forgeessentials.jscripting.ScriptInstance;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
    public LiteralArgumentBuilder<CommandSourceStack> setExecution()
    {
        return baseBuilder.then(Commands.literal("list").executes(CommandContext -> execute(CommandContext, "list")))
                .then(Commands.literal("reload").executes(CommandContext -> execute(CommandContext, "reload")))
                .then(Commands.literal("upgrade").executes(CommandContext -> execute(CommandContext, "upgrade")));
    }

    @Override
    public int execute(CommandContext<CommandSourceStack> ctx, String params) throws CommandSyntaxException
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
        	ChatOutputHandler.chatNotification(ctx.getSource(), "This is not ported/working/needed rn");
            //ScriptUpgrader.upgradeOldScripts(ctx.getSource());
            break;
        default:
            ChatOutputHandler.chatError(ctx.getSource(), FEPermissions.MSG_UNKNOWN_SUBCOMMAND, params);
            return Command.SINGLE_SUCCESS;
        }
        return Command.SINGLE_SUCCESS;
    }

    private static void parseReload(CommandContext<CommandSourceStack> ctx)
    {
        ChatOutputHandler.chatNotification(ctx.getSource(), "Reloading scripts...");
        ModuleJScripting.instance().reloadScripts(ctx.getSource());
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Done!");
    }

    private static void parseList(CommandContext<CommandSourceStack> ctx)
    {
        ChatOutputHandler.chatConfirmation(ctx.getSource(), "Loaded scripts:");
        for (ScriptInstance script : ModuleJScripting.getScripts())
        {
            ChatOutputHandler.chatError(ctx.getSource(), script.getName());

            List<String> eventHandlers = script.getEventHandlers();
            if (!eventHandlers.isEmpty())
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "  Registered events:");
                for (String eventType : eventHandlers)
                	ChatOutputHandler.chatWarning(ctx.getSource(), "    " + eventType);
            }

            Set<String> commands = script.getCommandNames();
            if (!commands.isEmpty())
            {
                ChatOutputHandler.chatNotification(ctx.getSource(), "  Registered commands:");
                for (String command : commands)
                	ChatOutputHandler.chatWarning(ctx.getSource(), "    /" + command);
            }
        }
    }
}