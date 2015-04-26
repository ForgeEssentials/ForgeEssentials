package com.forgeessentials.economy.commands.plots;

import java.util.Map.Entry;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.OutputHandler;

public class CommandListPlot extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        OutputHandler.chatNotification(sender, "Listing ALL plots:");
        for (AreaZone plot : PlotManager.getPlotList())
        {
            PlotManager.printPlotDetails(sender, plot);
        }
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args[0].equals("sale"))
        {
            if (args[1].equals("add"))
            {
                AreaZone zone = (AreaZone) APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[2]);
                PlotManager.pendingOffers.put(args[2], new Offer<AreaZone>(null, player, zone, Integer.parseInt(args[3])));
            }
            else if (args[1].equals("remove"))
            {
                PlotManager.pendingOffers.remove(args[2]);
            }
            else
            {
                OutputHandler.chatNotification(player, "Listing all plots for sale:");
                for (Entry<String, Offer<AreaZone>> offer : PlotManager.pendingOffers.entrySet())
                {
                    if (offer.getValue().buyer == null)
                    {
                        PlotManager.printPlotDetails(player, offer.getValue().item);
                    }
                }
            }
        }
        else
        {
            OutputHandler.chatNotification(player, "Listing ALL plots:");
            for (AreaZone plot : PlotManager.getPlotList())
            {
                PlotManager.printPlotDetails(player, plot);
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




}
