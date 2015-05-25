package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;

public class CommandHome extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "home";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 0)
        {
            WarpPoint home = PlayerInfo.get(sender.getPersistentID()).getHome();
            if (home == null)
                throw new TranslatedCommandException("No home set. Use \"/home set\" first.");
            TeleportHelper.teleport(sender, home);
        }
        else
        {
            if (args[0].equalsIgnoreCase("set"))
            {
                EntityPlayerMP player = sender;
                if (args.length == 2)
                {
                    if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_HOME_OTHER))
                        throw new TranslatedCommandException("You don't have the permission to access other players home.");
                    player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
                    if (player == null)
                        throw new TranslatedCommandException("Player %s not found.", args[1]);
                }
                else if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_HOME_SET))
                    throw new TranslatedCommandException("You don't have the permission to set your home location.");

                WarpPoint p = new WarpPoint(sender);
                PlayerInfo info = PlayerInfo.get(player.getPersistentID());
                info.setHome(p);
                info.save();
                OutputHandler.chatConfirmation(sender, Translator.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));
            }
            else
                throw new TranslatedCommandException("Unknown subcommand");
        }
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_HOME;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "here");
        }
        else
        {
            return null;
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        if (sender instanceof EntityPlayer)
        {
            return "/home [here|x, y, z] Set your home location.";
        }
        else
        {
            return null;
        }
    }

}
