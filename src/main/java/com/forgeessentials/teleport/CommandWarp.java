package com.forgeessentials.teleport;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.teleport.util.TeleportDataManager;
import com.forgeessentials.teleport.util.Warp;
import com.forgeessentials.util.AreaSelector.WarpPoint;
import com.forgeessentials.util.*;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntityCommandBlock;

import java.util.List;

/**
 * Now uses TeleportCenter.
 *
 * @author Dries007
 */

public class CommandWarp extends ForgeEssentialsCommandBase {
    @Override
    public String getCommandName()
    {
        return "warp";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            String msg = "";
            for (String warp : TeleportDataManager.warps.keySet())
            {
                msg = warp + ", " + msg;
            }
            ChatUtils.sendMessage(sender, msg);
        }
        else if (args.length == 1)
        {
            if (TeleportDataManager.warps.containsKey(args[0].toLowerCase()))
            {
                if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
                {
                    Warp warp = TeleportDataManager.warps.get(args[0].toLowerCase());
                    PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(sender.username);
                    playerInfo.back = new WarpPoint(sender);
                    CommandBack.justDied.remove(sender.username);
                    TeleportCenter.addToTpQue(warp.getPoint(), sender);
                }
                else
                {
                    OutputHandler.chatError(sender,
                            "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
                }
            }
            else
            {
                OutputHandler.chatError(sender, "That warp doesn't exist!");
            }
        }
        else if (args.length == 2)
        {
            if (APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + ".admin")))
            {
                if (args[0].equalsIgnoreCase("set"))
                {
                    if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
                    {
                        OutputHandler.chatError(sender, "That warp already exists. Use '/warp del <name>' to delete.");
                    }
                    else
                    {
                        TeleportDataManager.addWarp(new Warp(args[1].toLowerCase(), new WarpPoint(sender)));
                        OutputHandler.chatConfirmation(sender, "Done!");
                    }
                }
                else if (args[0].equalsIgnoreCase("del"))
                {
                    if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
                    {
                        TeleportDataManager.removeWarp(TeleportDataManager.warps.get(args[1]));
                        OutputHandler.chatConfirmation(sender, "Done!");
                    }
                    else
                    {
                        OutputHandler.chatError(sender, "That warp doesn't exist!");
                    }
                }
                else
                {
                    OutputHandler.chatError(sender, "Improper syntax. Please try this instead: [name] OR <set|del> <name> ");
                }
            }
            else
            {
                OutputHandler.chatError(sender,
                        "You have insufficient permission to do that. If you believe you received this message in error, please talk to a server admin.");
            }
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 2)
        {
            if (TeleportDataManager.warps.containsKey(args[1].toLowerCase()))
            {
                EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
                if (player != null)
                {
                    Warp warp = TeleportDataManager.warps.get(args[1].toLowerCase());
                    PlayerInfo.getPlayerInfo(player.username).back = new WarpPoint(player);
                    TeleportCenter.addToTpQue(warp.getPoint(), player);
                }
                else
                {
                    OutputHandler.chatError(sender, String.format("Player %s does not exist, or is not online.", args[0]));
                }
            }
            else
            {
                OutputHandler.felog.info("CommandBlock Error: That warp doesn't exist!");
            }
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public boolean canCommandBlockUseCommand(TileEntityCommandBlock te)
    {
        return true;
    }

    @Override
    public String getCommandPerm()
    {
        return "fe.teleport.warp";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsFromIterableMatchingLastWord(args, TeleportDataManager.warps.keySet());
        }
        else if (args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, "set", "del");
        }
        else
        {
            return null;
        }
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/warp [name] OR <set|del> <name> Teleports you to a warp point. You can also manipulate warps if you have permission.";
    }

}
