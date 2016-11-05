package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.util.ChatUtil;
import com.forgeessentials.util.ForgeEssentialsCommandBase;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.TranslatedCommandException;
import com.forgeessentials.util.Translator;
import com.forgeessentials.util.Utils;

public class CommandHome extends ForgeEssentialsCommandBase
{

    @Override
    public String getCommandName()
    {
        return "fehome";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "home" };
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

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_HOME;
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
                    if (!PermissionManager.checkPermission(sender, TeleportModule.PERM_HOME_OTHER))
                        throw new TranslatedCommandException("You don't have the permission to access other players home.");
                    player = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
                    if (player == null)
                        throw new TranslatedCommandException("Player %s not found.", args[1]);
                }
                else if (!PermissionManager.checkPermission(sender, TeleportModule.PERM_HOME_SET))
                    throw new TranslatedCommandException("You don't have the permission to set your home location.");

                WarpPoint p = new WarpPoint(sender);
                PlayerInfo info = PlayerInfo.get(player.getPersistentID());
                info.setHome(p);
                info.save();
                ChatUtil.chatConfirmation(sender, Translator.format("Home set to: %1.0f, %1.0f, %1.0f", p.getX(), p.getY(), p.getZ()));
            }
            else
                throw new TranslatedCommandException("Unknown subcommand");
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return Utils.getListOfStringsMatchingLastWord(args, "here");
        }
        else
        {
            return null;
        }
    }

}
