package com.forgeessentials.commands.server;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.FECommandManager.ConfigurableCommand;
import com.forgeessentials.util.OutputHandler;

public class CommandPing extends FEcmdModuleCommands implements ConfigurableCommand
{
    public String response = "Pong! %time";

    @Override
    public void loadConfig(Configuration config, String category)
    {
        response = config.get(category, "response", "Pong! %time").getString();
    }

    @Override
    public String getCommandName()
    {
        return "ping";
    }

    @Override
    public List<String> getCommandAliases()
    {
        return null;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        OutputHandler.chatNotification(sender, response.replaceAll("%time", sender.ping + "ms."));
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        OutputHandler.chatNotification(sender, response.replaceAll("%time", ""));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/ping Ping the server.";
    }
}
