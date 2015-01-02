package com.forgeessentials.economy.commands.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.economy.plots.PlotManager;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandEntitlement extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args.length == 0)
        {
            String cap = APIRegistry.perms.getPermissionProperty(player, PlotManager.PLOT_PERMPROP_CLAIMCAP);
            String used = APIRegistry.perms.getPermissionProperty(player, PlotManager.PLOT_PERMPROP_CLAIMED);
            OutputHandler.chatConfirmation(player, String.format("You are allowed to claim up to %s1 size of plots, of which you have used %s2", cap, used));
        }
        else if (args.length == 2 && args[1].equals("increase"))
        {

        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy.plots.caps";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "claimcap";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return null;
    }
}
