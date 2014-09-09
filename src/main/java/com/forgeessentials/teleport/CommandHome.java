package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.teleport.TeleportCenter;

public class CommandHome extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "home";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            WarpPoint home = PlayerInfo.getPlayerInfo(sender.getPersistentID()).getHome();
            if (home == null)
            {
                OutputHandler.chatError(sender, "No home set. Try this: [here|x, y, z]");
            }
            else
            {
                EntityPlayerMP player = (EntityPlayerMP) sender;
                PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
                playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                CommandBack.justDied.remove(player.getPersistentID());
                TeleportCenter.addToTpQue(home, player);
            }
        }
        else if (PermissionsManager.checkPerm(sender, getPermissionNode() + ".set"))
        {
            if (args.length >= 1 && (args[0].equals("here") || args[0].equals("set")))
            {
                WarpPoint p = new WarpPoint(sender);
                PlayerInfo.getPlayerInfo(sender.getPersistentID()).setHome(p);
                ChatUtils.sendMessage(sender, String.format("Home set to: %1$d, %2$d, %3$d", p.getX(), p.getY(), p.getZ()));
            }
        }
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.teleport." + getCommandName();
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
