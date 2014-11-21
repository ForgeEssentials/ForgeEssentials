package com.forgeessentials.multiworld.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

/**
 * @author Björn Zeutzheim
 */
public class CommandGetDimension extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "dim";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Get the ID of the dimension you are currently in.";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        OutputHandler.chatNotification(player, "You are in dimension " + player.dimension);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return null;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

}
