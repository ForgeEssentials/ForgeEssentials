package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.core.misc.Translator;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.ChatOutputHandler;

public class CommandTp extends ForgeEssentialsCommandBase
{

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<>();

    @Override
    public String getCommandName()
    {
        return "tp";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tp [player] <player|xyz>: Teleport to a location.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TP;
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        if (args.length == 1)
        {
            EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (PlayerSelector.hasArguments(args[0]))
            {
                target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            }
            if (target == null)
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            TeleportHelper.teleport(sender, new WarpPoint(target));
        }
        else if (args.length == 2 && PermissionManager.checkPermission(sender, TeleportModule.PERM_TP_OTHERS))
        {

            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);

                if (target != null)
                {
                    PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    WarpPoint point = new WarpPoint(target);
                    TeleportHelper.teleport(player, point);
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[1]);
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else if (args.length >= 3)
        {
            if (args.length == 3)
            {
                EntityPlayerMP player = sender;
                double x = parseDouble(player.posX, args[0], true);
                double y = ServerUtil.parseYLocation(sender, player.posY, args[1]);
                double z = parseDouble(player.posZ, args[2], true);
                PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
            }
            else if (args.length == 4)
            {
                EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (player != null)
                {
                    double x = parseDouble(player.posX, args[1], true);
                    double y = ServerUtil.parseYLocation(sender, player.posY, args[2]);
                    double z = parseDouble(player.posZ, args[3], true);
                    PlayerInfo playerInfo = PlayerInfo.get(player.getPersistentID());
                    playerInfo.setLastTeleportOrigin(new WarpPoint(player));
                    TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
                }
                else
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
            }
            else
                throw new TranslatedCommandException("Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
        }
        else
            throw new TranslatedCommandException("Improper syntax. Please try this instead: /tp [player] <player|<x> <y> <z>>");
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length == 2)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                EntityPlayer target = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
                if (PlayerSelector.hasArguments(args[1]))
                {
                    target = UserIdent.getPlayerByMatchOrUsername(sender, args[1]);
                }
                if (target == null)
                    throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[1]);
                TeleportHelper.teleport(player, new WarpPoint(target));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else if (args.length == 4)
        {
            EntityPlayerMP player = UserIdent.getPlayerByMatchOrUsername(sender, args[0]);
            if (player != null)
            {
                double x = parseDouble(player.posX, args[1], true);
                double y = ServerUtil.parseYLocation(sender, player.posY, args[2]);
                double z = parseDouble(player.posZ, args[3], true);
                TeleportHelper.teleport(player, new WarpPoint(player.dimension, x, y, z, player.rotationPitch, player.rotationYaw));
            }
            else
                throw new TranslatedCommandException("Player %s does not exist, or is not online.", args[0]);
        }
        else
        {
            ChatOutputHandler.chatError(sender, Translator.translate("Improper syntax. Please try this instead:"));
            ChatOutputHandler.chatNotification(sender, getCommandUsage(sender));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1 || args.length == 2)
        {
            return getListOfStringsMatchingLastWord(args, FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames());
        }
        else
        {
            return null;
        }
    }

}
