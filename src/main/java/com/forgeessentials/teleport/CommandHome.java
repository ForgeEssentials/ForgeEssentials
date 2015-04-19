package com.forgeessentials.teleport;

import java.util.List;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandHome extends ForgeEssentialsCommandBase {

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
            WarpPoint home = PlayerInfo.getPlayerInfo(sender.getPersistentID()).getHome();
            if (home == null)
            {
                OutputHandler.chatError(sender, "No home set. Use \"/home set\" first.");
                return;
            }
            EntityPlayerMP player = sender;
            PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
            playerInfo.setLastTeleportOrigin(new WarpPoint(player));
            CommandBack.justDied.remove(player.getPersistentID());
            TeleportHelper.teleport(player, home);
        }
        else
        {
            if (args[0].equalsIgnoreCase("set"))
            {
                EntityPlayerMP player = sender;
                if (args.length == 2)
                {
                    if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_HOME_OTHER))
                    {
                        OutputHandler.chatError(sender, "You don't have the permission to access other players home.");
                        return;
                    }
                    player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
                    if (player == null)
                    {
                        OutputHandler.chatError(sender, "Player not found.");
                        return;
                    }
                }
                else if (!PermissionsManager.checkPermission(sender, TeleportModule.PERM_HOME_SET))
                {
                    OutputHandler.chatError(sender, "You don't have the permission to set your home location.");
                    return;
                }

                WarpPoint p = new WarpPoint(sender);
                PlayerInfo info = PlayerInfo.getPlayerInfo(player.getPersistentID());
                info.setHome(p);
                info.save();
                OutputHandler.chatConfirmation(sender, String.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));
            }
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
