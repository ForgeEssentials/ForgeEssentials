package com.forgeessentials.economy.commands;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;

public class CommandMoney extends ForgeEssentialsCommandBase {
    @Override
    public List<String> getCommandAliases()
    {
        return Arrays.asList("wallet");
    }

    @Override
    public String getCommandName()
    {
        return "money";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        OutputHandler.chatConfirmation(sender, "Your wallet contains: " + APIRegistry.wallet.getMoneyString(sender.getPersistentID()));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.economy." + getCommandName();
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/money Get the amound of money you have in your wallet.";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }
}
