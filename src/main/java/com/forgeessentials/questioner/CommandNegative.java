package com.forgeessentials.questioner;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;

public class CommandNegative extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "no";
    }

    @Override
    public List<String> getCommandAliases()
    {
        ArrayList<String> list = new ArrayList<String>();
        list.add("decline");
        list.add("deny");
        list.add("take");
        return list;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        QuestionCenter.processAnswer(sender, false);
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public boolean canPlayerUseCommand(EntityPlayer player)
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.questioner.no";
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/no Answer no to a question";
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {

        return RegisteredPermValue.TRUE;
    }

}
