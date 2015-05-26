package com.forgeessentials.commands.game.player;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandFly extends FEcmdModuleCommands
{
    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

    @Override
    public String getCommandName()
    {
        return "fly";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/fly [true|false] Toggle flight mode.";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args.length == 0)
        {
            if (!player.capabilities.isFlying)
                player.capabilities.isFlying = true;
            else
                player.capabilities.isFlying = false;
        }
        else
        {
            player.capabilities.isFlying = Boolean.parseBoolean(args[0]);
        }
        if (!player.capabilities.isFlying)
            FunctionHelper.findSafeY(player);
        OutputHandler.chatNotification(player, "Flying " + (player.capabilities.isFlying ? "enabled" : "disabled"));
    }
}
