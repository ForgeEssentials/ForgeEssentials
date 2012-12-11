package com.ForgeEssentials.permission;

import com.ForgeEssentials.util.Localization;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;
import net.minecraftforge.event.Event.Result;

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
		Zone zone;
		
		switch(args.length)
		{
			case 3:
				zone = ZoneManager.GLOBAL;
			case 4:
				// check allow/deny part.
				Result result = parseAllow(args[2]);
				if (result.equals(Result.DEFAULT))
				{
					sender.sendChatToPlayer(Localization.format("message.error.illegalState", args[2]));
					return;
				}
				
				
				
				break;
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
