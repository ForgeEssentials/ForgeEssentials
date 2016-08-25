package com.forgeessentials.commands.server;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commands.ModuleCommands;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandPing extends ForgeEssentialsCommandBase implements ConfigurableCommand
{

    public String response = "Pong! %time";

    @Override
    public String getCommandName()
    {
        return "feping";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "ping" };
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleCommands.PERM + ".ping";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", sender.ping + "ms."));
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        ChatOutputHandler.chatNotification(sender, response.replaceAll("%time", ""));
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/ping Ping the server.";
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
