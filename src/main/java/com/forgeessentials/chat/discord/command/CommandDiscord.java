package com.forgeessentials.chat.discord.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

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
    @Override public String getPrimaryAlias()
    {
        return "discord";
    }

    @Override public String getUsage(ICommandSender sender)
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

    @Override public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.OP;
    }

    @Override public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args)  throws CommandException
    {
        if (args.length > 1)
        {
            CommandParserArgs _args = new CommandParserArgs(this, args, sender, server);
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
            ChatOutputHandler.chatError(sender, getUsage(sender));
        }
    }

    @Override public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args)
    {

    }
}
