package com.forgeessentials.teleport;

import java.util.HashMap;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.Point;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.teleport.TeleportCenter;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTppos extends ForgeEssentialsCommandBase {

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
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 3)
        {
            double x = parseDouble(sender, args[0], sender.posX);
            double y = parseDouble(sender, args[1], sender.posY);
            double z = parseDouble(sender, args[2], sender.posZ);
            EntityPlayerMP player = (EntityPlayerMP) sender;
            PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
            playerInfo.setLastTeleportOrigin(new WarpPoint(player));
            CommandBack.justDied.remove(player.getPersistentID());
            TeleportCenter.teleport(new WarpPoint(player.dimension, x, y, z, player.cameraPitch, player.cameraYaw), player);
        }
        else
        {
            this.error(sender);
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

    @SuppressWarnings("unchecked")
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
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/tppos <x y z> Teleport to a position.";
    }
}
