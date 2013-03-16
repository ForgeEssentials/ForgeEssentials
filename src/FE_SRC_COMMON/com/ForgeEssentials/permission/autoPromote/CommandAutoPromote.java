package com.ForgeEssentials.permission.autoPromote;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;

public class CommandAutoPromote extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "autopromote";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		//TODO: finish...
		if(true) return;
		/*
		 * Get the right zone.
		 * If nothing valid is given, defaults to the senders position.
		 */
		Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint(sender));
		if (args.length > 0)
		{
			if (args[0].equalsIgnoreCase("world"))
				zone = ZoneManager.getWorldZone(sender.worldObj);
			if (args[0].equalsIgnoreCase("global"))
				zone = ZoneManager.getGLOBAL();
			if (ZoneManager.doesZoneExist(args[0]))
				zone = ZoneManager.getZone(args[0]);
		}
		/*
		 * Need to make a new one?
		 */
		AutoPromote ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
		if (ap == null)
		{
			AutoPromoteManager.instance().map.put(zone.getZoneName(), new AutoPromote(zone.getZoneName(), false));
			ap = AutoPromoteManager.instance().map.get(zone.getZoneName());
		}
		/*
		 * Nope, Edit/vieuw/remove exisition one.
		 */
		if (args.length == 0 || args.length > 0 || args[1].equalsIgnoreCase("get"))
		{
			String header = "--- AutoPromote for: " + ap.zone + " ---";
			sender.sendChatToPlayer(header);
			sender.sendChatToPlayer("Enabled: " + ap.enable);
			sender.sendChatToPlayer("Promotion times: " + FunctionHelper.niceJoin(ap.promoteList.toArray()));
			StringBuilder footer = new StringBuilder();
			for (int i = 3; i < header.length(); i++) footer.append("-");
			sender.sendChatToPlayer(footer.toString());
			return;
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{	
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.autoPromote";
	}
	
	public List<String> getZoneNames()
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add("here");
		list.add("global");
		list.add("world");
		for (Zone zone : ZoneManager.getZoneList())
		{
			list.add(zone.getZoneName());
		}
		return list;
	}
	
	@Override
	public List<?> addTabCompletionOptions(ICommandSender sender, String[] args)
	{
		if (args.length == 1)
			return getListOfStringsFromIterableMatchingLastWord(args, getZoneNames());
		if (args.length == 2)
			return getListOfStringsFromIterableMatchingLastWord(args, getZoneNames());
		return null;
	}
	
}
