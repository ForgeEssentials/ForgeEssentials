package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.commons.selections.Point;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TranslatedCommandException;

public class CommandTppos extends ForgeEssentialsCommandBase
{

    /**
     * Spawn point for each dimension
     */
    public static HashMap<Integer, Point> spawnPoints = new HashMap<>();

    @Override
    public String getPrimaryAlias()
    {
        return "tppos";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {

        return "/tppos <x y z>: Teleport to a position.";
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public DefaultPermissionLevel getPermissionLevel()
    {
        return DefaultPermissionLevel.ALL;
    }

    @Override
    public String getPermissionNode()
    {
        return TeleportModule.PERM_TPPOS;
    }

    @Override
    public void processCommandPlayer(MinecraftServer server, EntityPlayerMP sender, String[] args) throws CommandException
    {
        throw new TranslatedCommandException("Not yet implemented!");
        // TODO 1.8 check
        // if (args.length == 3)
        // {
        // double x = func_110666_a(sender, sender.posX, args[0]);
        // double y = ServerUtil.parseYLocation(sender, sender.posY, args[1]);
        // double z = func_110666_a(sender, sender.posZ, args[2]);
        // TeleportHelper.teleport(sender, new WarpPoint(sender.dimension, x, y, z, sender.cameraPitch,
        // sender.cameraYaw));
        // }
        // else
        // {
        // throw new TranslatedCommandException(getUsage(sender));
        // }
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 1 || args.length == 2)
        {
            return matchToPlayers(args);
        }
        else
        {
            return null;
        }
    }

}
