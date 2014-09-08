package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.ChatUtils;

public class CommandMemusage extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "memusage";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        ChatUtils.sendMessage(sender, "Memory usage:");
        ChatUtils.sendMessage(sender, "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatUtils.sendMessage(sender, "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatUtils.sendMessage(sender, "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        ChatUtils.sendMessage(sender, "Memory usage:");
        ChatUtils.sendMessage(sender, "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        ChatUtils.sendMessage(sender, "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        ChatUtils.sendMessage(sender, "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/memusage Displays the JVM memoryusage.";
    }
}
