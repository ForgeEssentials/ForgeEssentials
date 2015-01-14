package com.forgeessentials.commands.admin;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import net.minecraftforge.server.command.ForgeCommand;

import java.text.DecimalFormat;

public class CommandServerPerf extends FEcmdModuleCommands {

    private static final DecimalFormat formatNumbers = new DecimalFormat("########0.000");

    @Override
    public String getCommandName()
    {
        return "memusage";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "perfstats" };
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        OutputHandler.chatNotification(sender, "Memory usage:");
        OutputHandler.chatNotification(sender, "Max: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + " MiB");
        OutputHandler.chatNotification(sender, "Total: " + (Runtime.getRuntime().totalMemory() / 1024 / 1024) + " MiB");
        OutputHandler.chatNotification(sender, "Free: " + (Runtime.getRuntime().freeMemory() / 1024 / 1024) + " MiB");
        long used = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        OutputHandler.chatNotification(sender, "Used: " + (used / 1024 / 1024) + " MiB");
        OutputHandler.chatNotification(sender, "Average tick time: " + formatNumbers.format(this.func_120035_a(MinecraftServer.getServer().tickTimeArray) * 1.0E-6D) + " ms");
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

        return "/perfstats Displays server performance stats (memory usage, average tick time).";
    }

    private double func_120035_a(long[] p_120035_1_)
    {
        long i = 0L;

        for (int j = 0; j < p_120035_1_.length; ++j)
        {
            i += p_120035_1_[j];
        }

        return (double)i / (double)p_120035_1_.length;
    }
}
