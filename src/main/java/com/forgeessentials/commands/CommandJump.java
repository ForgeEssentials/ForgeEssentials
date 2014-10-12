package com.forgeessentials.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.teleport.CommandBack;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.PlayerInfo;
import com.forgeessentials.util.selections.WarpPoint;
import com.forgeessentials.util.teleport.TeleportCenter;

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
            OutputHandler.chatError(sender, "Even jumpman couldn't make that jump!");
            return;
        }
        else
        {
        	EntityPlayerMP player = (EntityPlayerMP) sender;
			PlayerInfo playerInfo = PlayerInfo.getPlayerInfo(player.getPersistentID());
			playerInfo.setLastTeleportOrigin(new WarpPoint(player));
			CommandBack.justDied.remove(player.getPersistentID());
        	TeleportCenter.teleport(new WarpPoint(sender.getEntityWorld().provider.dimensionId, mo.blockX, mo.blockY, mo.blockZ, sender.rotationPitch, sender.rotationYaw), player);
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
