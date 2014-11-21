package com.forgeessentials.multiworld.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.core.MultiworldTeleporter;

/**
 * @author Björn Zeutzheim
 */
public class CommandMultiworldTeleport extends ForgeEssentialsCommandBase {

    @Override
    public String getCommandName()
    {
        return "mwtp";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender)
    {
        return "Teleport yourself or another player to a multiworld";
    }

    @Override
    public void processCommandPlayer(EntityPlayerMP player, String[] args)
    {
        try
        {
            int dimId = Integer.parseInt(args[0]);
            if (player.dimension == dimId)
                dimId = 0;
            if (DimensionManager.isDimensionRegistered(dimId))
            {
                if (dimId < 0 || dimId == 1)
                    throw new CommandException("You are not allowed to teleport to that dimension");
                WorldServer world = player.mcServer.worldServerForDimension(dimId);
                if (world != null)
                {
                    new MultiworldTeleporter(world).teleport(player);
                }
            }
            else
            {
                throw new CommandException("Dimension #" + args[0] + " does not exist.");
            }
        }
        catch (NumberFormatException e)
        {
            throw new CommandException("\"" + args[0] + "\" is not a valid number.");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return true;
    }

    @Override
    public String getPermissionNode()
    {
        return ModuleMultiworld.PERM_TELEPORT;
    }

    @Override
    public RegisteredPermValue getDefaultPermission()
    {
        return RegisteredPermValue.OP;
    }

}
