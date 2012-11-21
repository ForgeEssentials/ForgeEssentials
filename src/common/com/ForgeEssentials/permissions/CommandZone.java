package com.ForgeEssentials.permissions;

import java.util.Set;
import java.util.SortedSet;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.ICommandSender;

import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;

public class CommandZone extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		// TODO Auto-generated method stub
		return "zone";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		switch(args.length)
		{
			case 1:
			{
				if (args[0].equalsIgnoreCase("list"))
				{
					Set<String> set = ZoneManager.zoneMap.keySet();
					sender.sendChatToPlayer(" -- Zones List -- (1/"+((set.size()/15)+1)+")");
					int itterrator = 0;
					for (String zone : set)
					{
						if (itterrator == 15)
							break;
						sender.sendChatToPlayer(" -"+zone);
					}
					return;
				}
				else
					error(sender);
			}
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
		// no defining zones from the console.
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		// TODO no command for this from the console.
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.Permissions.Zone";
	}

}
