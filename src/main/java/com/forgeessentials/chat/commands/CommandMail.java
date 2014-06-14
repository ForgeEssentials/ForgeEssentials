package com.forgeessentials.chat.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.chat.Mail;
import com.forgeessentials.chat.MailSystem;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

public class CommandMail extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "mail";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("femail");
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (!Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()).contains(args[0]))
        {
            OutputHandler.chatError(sender, String.format("No player by the name: %s is registered on this server. Please try again.", args[0]));
            return;
        }
        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++)
        {
            cmd.append(args[i]);
            cmd.append(" ");
        }
        MailSystem.AddMail(new Mail("", sender.getCommandSenderName(), args[0], cmd.toString()));
        OutputHandler.chatConfirmation(sender, String.format("Posted message to %s.", args[0]));
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (!Arrays.asList(MinecraftServer.getServer().getConfigurationManager().getAvailablePlayerDat()).contains(args[0]))
        {
            OutputHandler.chatError(sender, String.format("No player by the name: %s is registered on this server. Please try again.", args[0]));
            return;
        }
        StringBuilder cmd = new StringBuilder(args.toString().length());
        for (int i = 1; i < args.length; i++)
        {
            cmd.append(args[i]);
            cmd.append(" ");
        }
        MailSystem.AddMail(new Mail("", sender.getCommandSenderName(), args[0], cmd.toString()));
        OutputHandler.chatConfirmation(sender, String.format("Posted message to %s.", args[0]));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.chat." + getCommandName();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

    @Override
    public int compareTo(Object o)
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/mail <player> <message> Sends a message to someone which can be read later.";
    }

    @Override
    public RegGroup getReggroup()
    {
        // TODO Auto-generated method stub
        return RegGroup.GUESTS;
    }

}
