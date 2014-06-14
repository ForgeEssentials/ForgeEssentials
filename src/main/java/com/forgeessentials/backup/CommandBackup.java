package com.forgeessentials.backup;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;

import java.io.File;
import java.util.List;

public class CommandBackup extends ForgeEssentialsCommandBase {
    static String source;
    static String output;
    static List<String> fileList;

    @Override
    public String getCommandName()
    {
        return "backup";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args)
    {
        Backup b = null;
        if (args.length != 1)
        {
            b = new Backup(true);
        }
        else
        {
            if (isInteger(args[0]))
            {
                b = new Backup(parseInt(sender, args[0]), true);
            }
            else if (args[0].equalsIgnoreCase("all"))
            {
                b = new Backup(((WorldServer) sender.getEntityWorld()).getChunkSaveLocation());

            }
            else if (!args[0].equalsIgnoreCase("all"))
            {
                b = new Backup(new File(args[0]));
            }
        }

        if (b != null)
        {
            b.startThread();
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer sender)
    {
        return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm()));
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.backup";
    }

    public static boolean isInteger(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/backup [dimID|foldername|all] Make a backup of everything or only specified folder/world.";
    }

    @Override
    public RegGroup getReggroup()
    {
        // TODO Auto-generated method stub
        return RegGroup.ZONE_ADMINS;
    }
}
