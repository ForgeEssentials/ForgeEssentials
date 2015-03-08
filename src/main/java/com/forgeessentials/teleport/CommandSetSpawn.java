package com.forgeessentials.teleport;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;

public class CommandSetSpawn extends ForgeEssentialsCommandBase {

    public static final String PERM_SETSPAWN = "fe.perm.setspawn";
    //public static final String PERM_SETSPAWN_OTHERS = "fe.perm.setspawn.others";
    
	@Override
	public String getCommandName()
	{
		return "setspawn";
	}

	@Override
	public void processCommandPlayer(EntityPlayerMP sender, String[] args)
	{
		if (args.length <= 0)
		{
		    OutputHandler.chatConfirmation(sender, "Usage: /setspawn here|bed|clear");
            // OutputHandler.chatConfirmation(sender, "For more spawn-control use /feperm command. Example:");
            // OutputHandler.chatConfirmation(sender, "  /p global spawn here|bed|clear");
		}
		else
		{
			UserIdent ident = new UserIdent(sender);
			switch (args[0].toLowerCase()) {
			case "here":
				APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.SPAWN_LOC, new WorldPoint(sender).toString());
				break;
			case "bed":
				APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.SPAWN_LOC, "bed");
				break;
			case "clear":
				APIRegistry.perms.getServerZone().clearPlayerPermission(ident, FEPermissions.SPAWN_LOC);
				break;
			default:
				throw new CommandException("Invalid location argument");
			}
		}
	}

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
            return getListOfStringsMatchingLastWord(args[0], new String[] { "here", "bed", "clear" });
        return null;
    }

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		throw new CommandException("This command cannot be used from console. Use \"/feperm user <USER> spawn\" instead");
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public String getPermissionNode()
	{
		return PERM_SETSPAWN;
	}

	@Override
	public RegisteredPermValue getDefaultPermission()
	{
		return RegisteredPermValue.OP;
	}

	@Override
	public String getCommandUsage(ICommandSender sender)
	{

		return "/setspawn help Set the spawn point.";
	}
}
