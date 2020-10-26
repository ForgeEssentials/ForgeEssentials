package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandRequestPayment extends ForgeEssentialsCommandBase
{

    @Override
    public String getPrimaryAlias()
    {
        return "requestpayment";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length != 2)
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amountRequested>");
        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
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
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length != 2)
            throw new TranslatedCommandException("Improper syntax. Please try this instead: <player> <amountRequested>");

        EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
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
    public String getUsage(ICommandSender sender)
    {

        return "/requestpayment <player> <amountRequested> Request a player to pay you a specified amount.";
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {

        return DefaultPermissionLevel.ALL;
    }
}
