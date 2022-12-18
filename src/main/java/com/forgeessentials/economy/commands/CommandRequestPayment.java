package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.BaseCommand;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class CommandRequestPayment extends BaseCommand
{

    public CommandRequestPayment(String name, int permissionLevel, boolean enabled)
    {
        super(name, permissionLevel, enabled);
    }

    @Override
    public String getPrimaryAlias()
    {
        return "requestpayment";
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> setExecution()
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int processCommandPlayer(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (args.length != 2)
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amountRequested>");
        ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, args[0] + " not found!");
        }
        else
        {
            int amount = parseInt(args[1], 0, Integer.MAX_VALUE);
            ChatOutputHandler.chatConfirmation(sender,
                    Translator.format("You requested %s to pay %s", player.getName(), APIRegistry.economy.toString(amount)));
            ChatOutputHandler.chatConfirmation(player,
                    Translator.format("You have been requested to pay %s by %s", APIRegistry.economy.toString(amount), sender.getName()));
        }
    }

    @Override
    public int processCommandConsole(CommandContext<CommandSource> ctx, Object... params) throws CommandSyntaxException
    {
        if (args.length != 2)
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amountRequested>");

        ServerPlayerEntity player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
        if (player == null)
        {
            ChatOutputHandler.chatError(sender, args[0] + " not found!");
        }
        else
        {
            int amount = parseInt(args[1], 0, Integer.MAX_VALUE);
            ChatOutputHandler.chatConfirmation(sender,
                    Translator.format("You requested %s to pay %s", player.getName(), APIRegistry.economy.toString(amount)));
            ChatOutputHandler
                    .chatConfirmation(player, Translator.format("You have been requested to pay %s by the server", APIRegistry.economy.toString(amount)));
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy." + getName();
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {

        return DefaultPermissionLevel.ALL;
    }
}
