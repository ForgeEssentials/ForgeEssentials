package com.forgeessentials.backup;

import java.io.File;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.world.WorldServer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandBackup extends ForgeEssentialsCommandBase {
    static String source;
    static String output;
    static List<String> fileList;

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
    public String getPermissionNode()
    {
        return "fe.backup";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/backup [dimID|foldername|all] Make a backup of everything or only specified folder/world.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.OP;
    }
}
