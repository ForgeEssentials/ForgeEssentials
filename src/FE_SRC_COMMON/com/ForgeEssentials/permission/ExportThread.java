package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.ForgeEssentials.util.ChatUtils;
import net.minecraft.command.ICommandSender;

import com.ForgeEssentials.api.permissions.Group;
import com.ForgeEssentials.util.OutputHandler;

@SuppressWarnings({ "unchecked" })
public class ExportThread extends Thread
{
	File			exportDir;
	ICommandSender	user;

	public ExportThread(String exportDir, ICommandSender sender)
	{
		this.exportDir = new File(ModulePermissions.permsFolder, exportDir);
		user = sender;
	}
	
	@Override
	public void run()
	{
		output("Exporting to " + exportDir.getName() + " folder");

		output("getting dump...");
		HashMap<String, Object> map = SqlHelper.dump();
		output("dump complete.");

		// make placeholder objects...
		Object obj1, obj2, obj3;

		output("Saving Permissions");
		obj1 = map.get("playerPerms");
		obj2 = map.get("groupPerms");
		FlatFilePermissions permissions = new FlatFilePermissions(exportDir);
		permissions.save((ArrayList<PermissionHolder>) obj1, (ArrayList<PermissionHolder>) obj2);

		output("Saving Permission Properties");
		obj1 = map.get("playerPermProps");
		obj2 = map.get("groupPermProps");
		FlatFilePermProps permProps = new FlatFilePermProps(exportDir);
		permProps.save((ArrayList<PermissionPropHolder>) obj1, (ArrayList<PermissionPropHolder>) obj2);

		output("Saving Groups");
		obj1 = map.get("groups");
		obj2 = map.get("ladders");
		obj3 = map.get("groupConnectors");
		FlatFileGroups groups = new FlatFileGroups(exportDir);
		groups.save((ArrayList<Group>) obj1, (ArrayList<PromotionLadder>) obj2, (HashMap<String, HashMap<String, ArrayList<String>>>) obj3);

		output("Saving Players");
		obj1 = map.get("players");
		FlatFilePlayers players = new FlatFilePlayers(exportDir);
		players.save((ArrayList<String>) obj1);

		output("Export Complete");
	}

	private void output(String msg)
	{
		if (user != null)
		{
			ChatUtils.sendMessage(user, "[PermSQL]" + msg);
		}
		else
		{
			OutputHandler.felog.finest(msg);
		}
	}
}
