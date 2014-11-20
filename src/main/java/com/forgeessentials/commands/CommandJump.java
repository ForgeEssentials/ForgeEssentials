package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.teleport.CommandBack;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.commons.selections.WarpPoint;

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
        MovingObjectPosition mo = FunctionHelper.getPlayerLookingSpot(sender, 500);
        if (mo == null)
        {
            OutputHandler.chatError(sender, "Even jumpman couldn't make that jump!");
            return;
        }
        else
        {
        	EntityPlayerMP player = (EntityPlayerMP) sender;
			PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
			playerInfo.setLastTeleportOrigin(new WarpPoint(player));
			CommandBack.justDied.remove(player.getPersistentID());
        	TeleportHelper.teleport(player, new WarpPoint(sender.getEntityWorld().provider.dimensionId, mo.blockX, mo.blockY + 1, mo.blockZ, sender.rotationPitch, sender.rotationYaw));
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
