package com.forgeessentials.economy.commands;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandSell extends ForgeEssentialsCommandBase
{
    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {

    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy.sell";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.TRUE;
    }

    @Override
    public String getCommandName()
    {
        return "sell";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return null;
    }
}
