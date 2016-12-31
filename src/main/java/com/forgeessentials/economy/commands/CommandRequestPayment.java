package com.forgeessentials.economy.commands;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.output.ChatOutputHandler;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandRequestPayment extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "requestpayment";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
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
            int amount = parseIntWithMin(sender, args[1], 0);
            ChatOutputHandler.chatConfirmation(sender,
                    Translator.format("You requested %s to pay %s", player.getCommandSenderName(), APIRegistry.economy.toString(amount)));
            ChatOutputHandler.chatConfirmation(player,
                    Translator.format("You have been requested to pay %s by %s", APIRegistry.economy.toString(amount), sender.getCommandSenderName()));
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
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
            int amount = parseIntWithMin(sender, args[1], 0);
            ChatOutputHandler.chatConfirmation(sender,
                    Translator.format("You requested %s to pay %s", player.getCommandSenderName(), APIRegistry.economy.toString(amount)));
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
        return "fe.economy." + getCommandName();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/requestpayment <player> <amountRequested> Request a player to pay you a specified amount.";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {

        return PermissionLevel.TRUE;
    }
}
