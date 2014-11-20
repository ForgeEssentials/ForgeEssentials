package com.forgeessentials.economy.commands.plots;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.PlayerInfo;

public class CommandSetPlot extends ForgeEssentialsCommandBase{

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        PlayerInfo info = PlayerInfo.getPlayerInfo(player);
        Plot plot = new Plot(player.getEntityWorld(), info.getPoint1(), info.getPoint2(), Integer.parseInt(args[1]), args[0], player.getUniqueID());
        PlotManager.addPlot(plot);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy.plots.set";
    }

    @Override
    public PermissionsManager.RegisteredPermValue getDefaultPermission()
    {
        return PermissionsManager.RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "setplot";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/setplot <name> <value> Set the current selection as a tradeable plot";
    }
}
