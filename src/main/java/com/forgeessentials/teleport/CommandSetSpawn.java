package com.forgeessentials.teleport;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.core.commands.ForgeEssentialsCommandBase;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.commons.selections.WorldPoint;
import cpw.mods.fml.common.FMLCommonHandler;

public class CommandSetSpawn extends ForgeEssentialsCommandBase {

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
		}
		else
		{
			UserIdent ident = new UserIdent(sender);
			switch (args[0].toLowerCase()) {
			case "here":
				APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.SPAWN, new WorldPoint(sender).toString());
				break;
			case "bed":
				APIRegistry.perms.setPlayerPermissionProperty(ident, FEPermissions.SPAWN, "bed");
				break;
			case "clear":
				APIRegistry.perms.getServerZone().clearPlayerPermission(ident, FEPermissions.SPAWN);
				break;
			default:
				throw new CommandException("Invalid location argument");
			}
		}
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
		return "fe.teleport." + getCommandName();
	}

    @Override
	public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		ArrayList<String> completes = new ArrayList<String>();

		// type
		if (args.length == 1)
		{
			completes.add("type");
			completes.add("point");
			completes.add("help");
		}
		// target type
		else if (args.length == 2)
		{
			completes.add("player");
			completes.add("group");
			completes.add("zone");
		}
		// target
		else if (args.length == 3)
		{
			if (args[1].equalsIgnoreCase("player"))
			{
				completes.add("_ME_");
				for (String name : FMLCommonHandler.instance().getMinecraftServerInstance().getAllUsernames())
				{
					completes.add(name);
				}

			}
			else if (args[1].equalsIgnoreCase("group"))
			{
				for (String group : APIRegistry.perms.getServerZone().getGroups())
				{
					completes.add(group);
				}
			}
			else if (args[1].equalsIgnoreCase("zone"))
			{
				for (Zone z : APIRegistry.perms.getZones())
				{
					completes.add(z.getName());
				}
			}
		}
		// value
		else if (args.length == 4)
		{
			if (args[0].equalsIgnoreCase("type"))
			{
				completes.add("none");
				completes.add("bed");
				completes.add("point");
			}
			else if (args[0].equalsIgnoreCase("point"))
			{
				completes.add("here");
			}
		}
		// zone after 1 arg of vals
		else if (args.length == 5)
		{
			if (args[0].equalsIgnoreCase("type") || (args[0].equalsIgnoreCase("point") && args[4].equalsIgnoreCase("here")))
			{
				for (Zone z : APIRegistry.perms.getZones())
				{
					completes.add(z.getName());
				}
			}
		}
		// zone after coords
		else if (args.length == 7)
		{
			if (args[0].equalsIgnoreCase("point"))
			{
				for (Zone z : APIRegistry.perms.getZones())
				{
					completes.add(z.getName());
				}
			}
		}

		return getListOfStringsMatchingLastWord(args, completes.toArray(new String[completes.size()]));

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
