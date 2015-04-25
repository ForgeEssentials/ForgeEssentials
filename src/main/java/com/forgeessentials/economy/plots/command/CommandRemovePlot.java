package com.forgeessentials.economy.plots.command;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.PlotManager;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permissions.PermissionsManager;

public class CommandRemovePlot extends ForgeEssentialsCommandBase{

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        Zone zone = APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[0]);
        zone.getServerZone().removeZone(zone);
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
