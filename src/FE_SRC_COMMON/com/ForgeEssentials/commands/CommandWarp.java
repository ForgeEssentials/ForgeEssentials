package com.ForgeEssentials.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permission.PermissionsAPI;
import com.ForgeEssentials.permission.query.PermQueryPlayer;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.TeleportCenter;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

/**
 * Now uses TeleportCenter.
 * TODO get rid of DataStorage
 * 
 * @author Dries007
 *
 */

public class CommandWarp extends ForgeEssentialsCommandBase
{
	@Override
	public String getCommandName()
	{
		return "warp";
	}

	@Override
	public void processCommandPlayer(EntityPlayer sender, String[] args)
	{
		NBTTagCompound warpdata = DataStorage.getData("warpdata");
		if(args.length == 0)
		{
			sender.sendChatToPlayer(Localization.get("command.warp.list"));
			String msg = "";
			for(Object temp : warpdata.getTags())
			{
				NBTTagCompound warp = (NBTTagCompound) temp;
				msg = warp.getName() + ", " + msg;
			}
			sender.sendChatToPlayer(msg);
		}
		else if(args.length == 1)
		{
			if(warpdata.hasKey(args[0].toLowerCase()))
			{
				if(true)
				if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					NBTTagCompound warp = warpdata.getCompoundTag(args[0].toLowerCase());
					TeleportCenter.addToTpQue(new WarpPoint(warp.getInteger("dim"), (int)warp.getDouble("X"), (int)warp.getDouble("Y"), (int)warp.getDouble("Z"), warp.getFloat("Yaw"), warp.getFloat("Pitch")), sender);
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get("command.warp.notfound"));
			}
		}
		else if(args.length == 2)
		{
			if(true)
			if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "admin")))
			{
				if(args[0].equalsIgnoreCase("set"))
				{
					if(warpdata.hasKey(args[1].toLowerCase()))
					{
						OutputHandler.chatError(sender, Localization.get("command.warp.alreadyexists"));
					}
					else
					{
						NBTTagCompound warp = new NBTTagCompound();
							warp.setDouble("X", sender.posX);
							warp.setDouble("Y", sender.posY);
							warp.setDouble("Z", sender.posZ);
							warp.setFloat("Yaw", sender.rotationYaw);
							warp.setFloat("Pitch", sender.rotationPitch);
							warp.setInteger("dim", sender.dimension);
						warpdata.setCompoundTag(args[1].toLowerCase(), warp);
						
						OutputHandler.chatConfirmation(sender, Localization.get(Localization.DONE));
					}
				}
				else if(args[0].equalsIgnoreCase("del"))
				{
					if(warpdata.hasKey(args[1].toLowerCase()))
					{
						warpdata.removeTag(args[1].toLowerCase());
						OutputHandler.chatConfirmation(sender, Localization.get(Localization.DONE));
					}
					else
					{
						OutputHandler.chatError(sender, Localization.get("command.warp.notfound"));
					}
				}
				else
				{
					OutputHandler.chatError(sender, Localization.get(Localization.ERROR_BADSYNTAX) + getSyntaxPlayer(sender));
				}
			}
			else
			{
				OutputHandler.chatError(sender, Localization.get(Localization.ERROR_PERMDENIED));
			}
			DataStorage.setData("warpdata", warpdata);
		}
	}

	@Override
	public void processCommandConsole(ICommandSender sender, String[] args)
	{
	}

	@Override
	public boolean canConsoleUseCommand()
	{
		return false;
	}

	@Override
	public boolean canPlayerUseCommand(EntityPlayer player)
	{
		return true;
	}

	@Override
	public String getCommandPerm()
	{
		return "ForgeEssentials.BasicCommands." + getCommandName();
	}
	
	@Override
	public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
		NBTTagCompound warps = DataStorage.getData("warpdata");
    	Iterator warpsIt = warps.getTags().iterator();
    	List<String> list = new ArrayList<String>();
    	while(warpsIt.hasNext()) {
    		NBTTagCompound buffer = (NBTTagCompound) warpsIt.next();
    		list.add(buffer.getName());
    	}
    	
    	if(args.length == 1)
    	{
    		return getListOfStringsFromIterableMatchingLastWord(args, list);
    	}
    	else if(args.length == 2)
    	{
    		return getListOfStringsMatchingLastWord(args, "set", "del");
    	}
    	else
    	{
    		return null;
    	}
    }

}
