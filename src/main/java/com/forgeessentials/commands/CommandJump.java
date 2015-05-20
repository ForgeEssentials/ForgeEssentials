package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.FunctionHelper;

public class CommandJump extends FEcmdModuleCommands
{

    @Override
    public String getCommandName()
    {
        return "jump";
    }

    @Override
    public String[] getDefaultAliases()
    {
        return new String[] { "j" };
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args)
    {
        MovingObjectPosition mo = FunctionHelper.getPlayerLookingSpot(sender, 500);
        if (mo == null)
            throw new TranslatedCommandException("The spot you are looking at is too far away to teleport.");
        TeleportHelper.teleport(sender, new WarpPoint(sender.getEntityWorld().provider.dimensionId, mo.blockX, mo.blockY + 1, mo.blockZ, sender.rotationPitch,
                sender.rotationYaw));
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
        return "/jump Teleport to the location you are looking at.";
    }
}
