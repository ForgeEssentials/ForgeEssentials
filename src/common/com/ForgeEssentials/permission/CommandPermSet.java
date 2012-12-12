package com.ForgeEssentials.permission;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.util.Localization;

public class CommandPermSet extends CommandFEPermBase
{

	@Override
	public String getCommand()
	{
		// TODO Auto-generated method stub
		return "set";
	}
	
	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		Zone zone;
		
		switch(args.length)
		{
			case 3:
				zone = ZoneManager.GLOBAL;
			case 4:
				Result result = parseAllow(args[2]);
				if (result.equals(Result.DEFAULT))
				{
					sender.sendChatToPlayer("");
				}
				
				break;
			default:
				this.error(sender);
				return;
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		Zone zone = ZoneManager.GLOBAL;
		
		switch(args.length)
		{
			case 4:
				if (!ZoneManager.doesZoneExist(args[2]))
				{
					sender.sendChatToPlayer(Localization.format("message.error.nozone", args[3]));
					return;
				}
				zone = ZoneManager.getZone(args[3]);
				
				break;
			case 3:			
				// check allow/deny part.
				Result result = parseAllow(args[1]);
				if (result.equals(Result.DEFAULT))
				{
					sender.sendChatToPlayer(Localization.format("message.error.illegalState", args[1]));
					return;
				}
				
				// check Groups.
				String[] entities = args[2].split(":");
				if (entities.length != 2)
					sender.sendChatToPlayer(Localization.format("message.error.illegalEntity", args[2]));
				
				if (entities[0].equalsIgnoreCase("g"))
				{
					//GroupManager.
					PermissionsAPI.setGroupPermission(entities[2], args[1], result.equals(Result.ALLOW), zone.getZoneID());
				}
				else if (entities[0].equalsIgnoreCase("p"))
				{
					PermissionsAPI.setPlayerPermission(entities[2], args[1], result.equals(Result.ALLOW), zone.getZoneID());
				}
				else
					sender.sendChatToPlayer(Localization.format("message.error.illegalEntity", args[2]));
			default:
				this.error(sender);
				return;
		}
	}
	
	private Result parseAllow(String value)
	{
		if (value.equalsIgnoreCase("allow") || value.equalsIgnoreCase("allowed") || value.equalsIgnoreCase("true"))
			return Result.ALLOW;
		else if (value.equalsIgnoreCase("deny") || value.equalsIgnoreCase("denied") || value.equalsIgnoreCase("false"))
			return Result.DENY;
		else
			return Result.DEFAULT;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.permissions.set";
	}
}
