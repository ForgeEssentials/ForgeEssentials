package com.ForgeEssentials.permission;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityCommandBlock;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.query.PermQuery.PermResult;
import com.ForgeEssentials.permission.query.PermQueryBlanketSpot;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

import cpw.mods.fml.common.FMLCommonHandler;

public class CommandFEPerm extends ForgeEssentialsCommandBase
{
	//Variables for autocomplete
	String [] args2 =  {"user", "group", "export", "promote"};
	String [] groupargs = {"prefix", "suffix", "parent", "priority","allow","true","deny","false","clear"};
	String [] playerargs = {"prefix", "suffix", "group","allow","true","deny","false","clear"};
	String [] playergroupargs = {"set","add","remove"};
	@Override
	public final String getCommandName()
	{
		return "feperm";
	}

	@Override
	public List getCommandAliases()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("perm");
		list.add("fep");
		list.add("p");
		return list;
	}

	@Override
	public String getCommandSyntax(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.syntax");
	}

	@Override
	public String getCommandInfo(ICommandSender sender)
	{
		return Localization.get("command.permissions.feperm.info");
	}

	// ------------------------------------------
	// -------STUFF-THAT-DOESNT-MATTER-----------
	// ------------------------------------------

	@Override
	public String getInfoPlayer(EntityPlayer player)
	{
		return null;
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public boolean canCommandBlockUseCommand(TileEntityCommandBlock block)
	{
		PermResult result = PermissionsAPI.checkPermResult(new PermQueryBlanketSpot(new WorldPoint(block.worldObj, block.xCoord, block.yCoord, block.zCoord),
				getCommandPerm(), true));
		return result.equals(PermResult.DENY) ? false : true;
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		if(args.length == 0)
		{
			OutputHandler.chatConfirmation(sender, "Base usage is /p user|group.");
			OutputHandler.chatConfirmation(sender, "Type one of these for more information.");
			return;
		}
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandFEPermUser.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("group"))
		{
			CommandFEPermGroup.processCommandPlayer(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("promote"))
		{
			CommandFEPermPromote.processCommandPlayer(sender, newArgs);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		if(args.length == 0)
		{
			sender.sendChatToPlayer("Base usage is /p user|group.");
			sender.sendChatToPlayer("Type one of these for more information.");
			return;
		}
		String first = args[0];
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++)
		{
			newArgs[i] = args[i + 1];
		}

		if (first.equalsIgnoreCase("user") || first.equalsIgnoreCase("player"))
		{
			CommandFEPermUser.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("export"))
		{
			CommandExport.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("group"))
		{
			CommandFEPermGroup.processCommandConsole(sender, newArgs);
		}
		else if (first.equalsIgnoreCase("promote"))
		{
			CommandFEPermPromote.processCommandConsole(sender, newArgs);
		}
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.perm";
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		PermResult result = PermissionsAPI.checkPermResult(new PermQueryPlayer(player, getCommandPerm(), true));
		return result.equals(PermResult.DENY) ? false : true;
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1) {
			return getListOfStringsMatchingLastWord(args, args2);
		}
		
		else {
	        
		}
		switch (args.length){
			case 1:
				return getListOfStringsMatchingLastWord(args, args2);
			case 2:
				if (args[0].equalsIgnoreCase("group")) {
					List<Group> groups = PermissionsAPI.getGroupsInZone(ZoneManager.GLOBAL.getZoneName());
					ArrayList<String> groupnames = new ArrayList<String>();
					for (int i = 0; i < groups.size(); i++) {
						groupnames.add(groups.get(i).name);
					}
					groupnames.add("create");
					return getListOfStringsFromIterableMatchingLastWord(args, groupnames);
				}
				break;
			case 3:
				if (args[0].equalsIgnoreCase("user") || args[0].equalsIgnoreCase("player")) {
					return getListOfStringsMatchingLastWord(args, playerargs);
				}
				else if (args[0].equalsIgnoreCase("group") && !args[1].equalsIgnoreCase("create")) {
					return getListOfStringsMatchingLastWord(args, groupargs);
				}
				break;
			case 4:
				if (args[0].equalsIgnoreCase("user") && (args[2].equalsIgnoreCase("group"))) {
					return getListOfStringsMatchingLastWord(args, playergroupargs);
				}
				break;
			case 5:
				if (args[0].equalsIgnoreCase("user") && (args[2].equalsIgnoreCase("group"))) {
					List<Group> groups = PermissionsAPI.getGroupsInZone(ZoneManager.GLOBAL.getZoneName());
					ArrayList<String> groupnames = new ArrayList<String>();
					for (int i = 0; i < groups.size(); i++) {
						groupnames.add(groups.get(i).name);
					}
					groupnames.add("create");
					return getListOfStringsFromIterableMatchingLastWord(args, groupnames);
				}
				break;
		}
		return FMLCommonHandler.instance().getSidedDelegate().getServer().getPossibleCompletions(sender, args[args.length - 1]);
	}
	
}
