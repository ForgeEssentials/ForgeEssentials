package com.forgeessentials.teleport;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.commons.selections.WarpPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.core.misc.TeleportHelper;
import com.forgeessentials.core.misc.TranslatedCommandException;
import com.forgeessentials.util.PlayerUtil;

public class CommandJump extends ForgeEssentialsCommandBase
{
    @Override
    public String getCommandName()
    {
        return "jump";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP sender, String[] args) throws CommandException
    {
        MovingObjectPosition mo = PlayerUtil.getPlayerLookingSpot(sender, 500);
        if (mo == null)
            throw new TranslatedCommandException("The spot you are looking at is too far away to teleport.");
        BlockPos pos = mo.func_178782_a();
        TeleportHelper.teleport(sender, new WarpPoint(sender.getEntityWorld().provider.getDimensionId(), pos.getX(), pos.getY() + 1, pos.getZ(),
                sender.rotationPitch, sender.rotationYaw));
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public String getPermissionNode()
    {
        return "fe.teleport.jump";
    }

    @Override
    public PermissionLevel getPermissionLevel()
    {
        return PermissionLevel.OP;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {
        return "/jump Teleport to the location you are looking at.";
    }
}
