package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.selections.WarpPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandTop extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "top";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0)
        {
            top(sender);
        }
        else if (args.length == 1 && PermissionsManager.checkPerm(sender, getPermissionNode() + ".others"))
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                top(player);
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <player>");
        }
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            EntityPlayerMP player = FunctionHelper.getPlayerForName(sender, args[0]);
            if (player != null)
            {
                top(player);
            }
            else
            {
                ChatUtils.sendMessage(sender, String.format("Player %s does not exist, or is not online.", args[0]));
            }
        }
        else
        {
            ChatUtils.sendMessage(sender, "Improper syntax. Please try this instead: <player>");
        }
    }

    public void top(EntityPlayer player)
    {
        WarpPoint point = new WarpPoint(player);
        point.setY(player.worldObj.getActualHeight());
        while (player.worldObj.getBlock(point.getX(), point.getY(), point.getZ()) == Blocks.air)
        {
            point.setY(point.getY() - 1);
        }
        ((EntityPlayerMP) player).playerNetServerHandler.setPlayerLocation(point.getX(), point.getY() + 1, point.getZ(), point.yaw, point.pitch);
        ChatUtils.sendMessage(player, "Teleported.");
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.teleport.top";
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
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

        return "/top <player> Teleport you or another player to the top of the world.";
    }

}
