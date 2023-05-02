package com.forgeessentials.chat.discord.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.chat.ModuleChat;
import com.forgeessentials.chat.discord.DiscordHandler;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.CommandParserArgs;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandDiscord extends ForgeEssentialsCommandBase
{

    private DiscordHandler handler;

    public CommandDiscord(DiscordHandler handler)
    {
        this.handler = handler;
    }
    @Override public String getCommandName()
    {
        return "discord";
    }

    @Override public String getCommandUsage(ICommandSender sender)
    {
        return "/discord select [channel]";
    }

    @Override public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override public String getPermissionNode()
    {
        return ModuleChat.PERM + ".discord";
    }

    @Override public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length > 1)
        {
            CommandParserArgs _args = new CommandParserArgs(this, args, sender);
            String command = _args.remove();
            if ("select".equals(command))
            {
                String channel = _args.remove();
                if (handler.channels.contains(channel)) {
                    handler.selectedChannel = channel;
                    ChatOutputHandler.chatConfirmation(sender, Translator.format("Channel #%s selected!"));
                } else {
                    ChatOutputHandler.chatError(sender, Translator.format("Unknown Channel: %s", channel));
                }
            }
        } else {
            ChatOutputHandler.chatError(sender, getCommandUsage(sender));
        }
    }

    @Override public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {

    }
}
