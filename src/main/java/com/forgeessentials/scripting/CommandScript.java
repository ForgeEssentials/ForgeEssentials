package com.forgeessentials.scripting;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

public class CommandScript extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "script";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args[0].equalsIgnoreCase("run"))
        {
            EventType e = EventType.valueOf(args[1].toUpperCase());
            if (args[2] != null)
            {
                EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[2]);
                EventType.run(player, e);
            }
            else
            {
                EventType.run(sender, e);
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args[0].equalsIgnoreCase("run"))
        {
            EventType e = EventType.valueOf(args[1].toUpperCase());
            EntityPlayer player = FunctionHelper.getPlayerForName(sender, args[2]);
            EventType.run(player, e);

        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.script";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/script run [login|respawn] <player> Manually trigger scripts for a player.";
    }

    @Override
    public RegGroup getReggroup()
    {

        return RegGroup.ZONE_ADMINS;
    }

}
