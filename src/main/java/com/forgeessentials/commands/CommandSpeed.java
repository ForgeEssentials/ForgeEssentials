package com.forgeessentials.commands;

import com.forgeessentials.commands.network.S6PacketSpeed;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandSpeed extends FEcmdModuleCommands
{
    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        if (args.length == 1)
        {
            float speed = Float.parseFloat(args[0]);
            FunctionHelper.netHandler.sendTo(new S6PacketSpeed(speed), player);
        }
    }

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
        return "speed";
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_)
    {
        return "/speed [player] <speed> Set or change the player's speed.";
    }
}
