package com.forgeessentials.commands.server;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPing extends ForgeEssentialsCommandBase implements ConfigurableCommand
{
    public String response = "Pong! %time";

    @Override
    public String getPrimaryAlias()
    {
        return "ping";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "/ping Ping the server.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".ping";
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", sender.ping + "ms."));
    }

    @Override
    public void processCommandConsole(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", ""));
    }

    @Override
    public void loadConfig(Configuration config, String category)
    {
        response = config.get(category, "response", "Pong! %time").getString();
    }

    @Override
    public void loadData()
    {
        /* do nothing */
    }

}
