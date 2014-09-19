package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.PlotManager;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager;

public class CommandRemovePlot extends ForgeEssentialsCommandBase{

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        PlotManager.removePlot(args[0]);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy.plots.remove";
    }

    @Override
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "removeplot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/removeplot <name> Removes a plot.";
    }
}
