package com.forgeessentials.commands;

import com.forgeessentials.commands.network.S6PacketSpeed;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

public class CommandSpeed extends FEcmdModuleCommands
{
    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        OutputHandler.chatWarning(player, "Here be dragons. Proceed at own risk. Use /speed reset to reset your speed..");
        if (args.length == 1)
        {
            //float speed = Float.parseFloat(args[0]);

            if (args[0].equals("reset"))
            {
                OutputHandler.chatNotification(player, "Resetting speed to regular walking speed.");
                FunctionHelper.netHandler.sendTo(new S6PacketSpeed(0.0F), player);
                return;
            }

            float speed = 0.05F;

            int multiplier = Integer.parseInt(args[0]);

            if (multiplier >= 10)
            {
                OutputHandler.chatWarning(player, "Multiplier set too high. Bad things may happen, so we're throttling your speed to 10x walking speed.");
                multiplier = 10;
            }
            speed = speed * multiplier;
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
