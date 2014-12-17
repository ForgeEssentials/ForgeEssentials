package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.PlotManager.Offer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.PlotManager;
import com.forgeessentials.util.OutputHandler;
import java.util.Map.Entry;

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
                Zone zone = APIRegistry.perms.getZoneById(PlotManager.PLOT_NAME_ID + args[2]);
                PlotManager.pendingOffers.put(args[2], new Offer(zone, null, player, Integer.parseInt(args[3])));
            }
            else if (args[1].equals("remove"))
            {
                PlotManager.pendingOffers.remove(args[2]);
            }
            else
            {
                OutputHandler.chatNotification(player, "Listing all plots for sale:");
                for (Entry<String, Offer> offer : PlotManager.pendingOffers.entrySet())
                {
                    if (offer.getValue().buyer == null)
                    {
                        PlotManager.printPlotDetails(player, (AreaZone) offer.getValue().plot);
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
