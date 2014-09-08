package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class CommandJump extends FEcmdModuleCommands {

    @Override
    public String getCommandName()
    {
        return "jump";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[]
                { "j" };
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        MovingObjectPosition mo = FunctionHelper.getPlayerLookingSpot(sender, false);
        if (mo == null)
        {
            OutputHandler.chatError(sender, "command.jump.toofar");
            return;
        }
        else
        {
            ((EntityPlayerMP) sender).playerNetServerHandler
                    .setPlayerLocation(mo.blockX + .5, mo.blockY + 1, mo.blockZ + .5, sender.rotationPitch, sender.rotationYaw);
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
    public String getCommandUsage(ICommandSender sender)
    {

        return "/jump Jump.";
    }
}
