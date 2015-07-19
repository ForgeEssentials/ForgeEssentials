package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.ServerUtil;

import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommandTppos extends ForgeEssentialsCommandBase
{

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<Integer, Point>();

    @Override
    public String getCommandName()
    {
        return "tppos";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        if (args.length == 3)
        {
            double x = func_110666_a(sender, sender.posX, args[0]);
            double y = ServerUtil.parseYLocation(sender, sender.posY, args[1]);
            double z = func_110666_a(sender, sender.posZ, args[2]);
            TeleportHelper.teleport(sender, new WarpPoint(sender.dimension, x, y, z, sender.cameraPitch, sender.cameraYaw));
        }
        else
        {
            throw new TranslatedCommandException(getCommandUsage(sender));
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
        return TeleportModule.PERM_TPPOS;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
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

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tppos <x y z> Teleport to a position.";
    }
}
