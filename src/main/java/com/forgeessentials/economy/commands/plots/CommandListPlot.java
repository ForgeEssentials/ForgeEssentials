package com.forgeessentials.economy.commands.plots;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.Plot;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandListPlot extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        OutputHandler.chatNotification(sender, "Listing ALL plots:");
        for (Plot plot : PlotManager.plotList.values())
        {
            printPlotDetails(sender, plot);
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args[0].equals("sale"))
        {
            if (args[1].equals("add"))
            {
                PlotManager.forSale.add(PlotManager.plotList.get(args[2]).getName());
            }
            else if (args[1].equals("remove"))
            {
                PlotManager.forSale.remove(PlotManager.plotList.get(args[2]).getName());
            }
            else
            {
                OutputHandler.chatNotification(player, "Listing all plots for sale:");
                for (String s : PlotManager.forSale)
                {
                    Plot plot = PlotManager.plotList.get(s);
                    printPlotDetails(player, plot);
                }
            }
        }
        else
        {
            OutputHandler.chatNotification(player, "Listing ALL plots:");
            for (Plot plot : PlotManager.plotList.values())
            {
                printPlotDetails(player, plot);
            }
        }

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.commands.plot.list";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "plotlist";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/plotlist [sale] [add|remove] [plotName]";
    }

    private void printPlotDetails(ICommandSender sender, Plot plot)
    {
        OutputHandler.chatNotification(sender, "Name: " + plot.getName()
                + " Owner: " + UserIdent.getUsernameByUuid(plot.getOwner())
                + "Location: between " + plot.getHighPoint().toString() + " and " + plot.getLowPoint().toString());
    }


}
