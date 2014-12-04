package com.forgeessentials.multiworld.command;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.multiworld.ModuleMultiworld;
import com.forgeessentials.multiworld.Multiworld;
import com.forgeessentials.util.OutputHandler;

/**
 * @author Olee
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
        int dimId = 0;

        Multiworld multiworld = null;
        if (args.length > 0)
        {
            multiworld = ModuleMultiworld.getMultiworldManager().getWorld(args[0]);
            if (multiworld == null)
                throw new CommandException("Multiworld " + args[0] + " does not exist.");
            dimId = multiworld.getDimensionId();
        }

        if (!DimensionManager.isDimensionRegistered(dimId))
            throw new CommandException("Dimension #" + args[0] + " does not exist.");

        if (dimId == player.dimension)
            throw new CommandException("You are already in that dimension");
        if (dimId < 0 || dimId == 1)
            throw new CommandException("You are not allowed to teleport to that dimension");

        WorldServer world = player.mcServer.worldServerForDimension(dimId);

        String msg = "Teleporting to ";
        if (multiworld == null)
        {
            switch (dimId)
            {
            case 0:
                msg += "the overworld";
                break;
            case 1:
                msg += "the nether";
                break;
            case -1:
                msg += "the end";
                break;
            default:
                msg += " dimension #" + dimId;
                break;
            }
        }
        else
        {
            msg += multiworld.getName();
        }
        OutputHandler.chatConfirmation(player, msg);
        Multiworld.teleport(player, world);
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
