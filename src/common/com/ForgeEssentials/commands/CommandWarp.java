package com.ForgeEssentials.commands;

import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.core.commands.ForgeEssentialsCommandBase;
import com.ForgeEssentials.permissions.PermQueryPlayer;
import com.ForgeEssentials.permissions.PermissionsAPI;
import com.ForgeEssentials.util.DataStorage;
import com.ForgeEssentials.util.Localization;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.AreaSelector.Point;

import cpw.mods.fml.common.FMLCommonHandler;

import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ICommandSender;
import net.minecraft.src.NBTTagCompound;

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
				if(PermissionsAPI.checkPermAllowed(new PermQueryPlayer(sender, getCommandPerm() + "." + args[0].toLowerCase())))
				{
					NBTTagCompound warp = warpdata.getCompoundTag(args[0].toLowerCase());
					if(sender.dimension != warp.getInteger("dim"))
					{
						FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().transferPlayerToDimension((EntityPlayerMP)sender, warp.getInteger("dim"));
					}
					((EntityPlayerMP) sender).playerNetServerHandler.setPlayerLocation(warp.getDouble("X"), warp.getDouble("Y"), warp.getDouble("Z"), warp.getFloat("Yaw"), warp.getFloat("Pitch"));
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
		}
		else
		{
			
		}
		
		DataStorage.setData("warpdata", warpdata);
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

}
